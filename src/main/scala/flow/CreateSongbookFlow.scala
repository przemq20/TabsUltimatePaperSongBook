package flow

import model.Song
import org.apache.pekko.NotUsed
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.FlowShape
import org.apache.pekko.stream.RestartSettings
import org.apache.pekko.stream.scaladsl.Broadcast
import org.apache.pekko.stream.scaladsl.Flow
import org.apache.pekko.stream.scaladsl.GraphDSL
import org.apache.pekko.stream.scaladsl.GraphDSL.Implicits._
import org.apache.pekko.stream.scaladsl.Merge
import org.apache.pekko.stream.scaladsl.RestartFlow
import org.apache.pekko.stream.scaladsl.Sink
import org.apache.pekko.stream.scaladsl.Source
import parsers.SongListParser
import parsers.SongParser
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.Success
import songBook.SongBookCreator
import songBook.SongBookWithChordsCreator
import songBook.SongBookWithoutChordsCreator

class CreateSongbookFlow {
  implicit val system: ActorSystem = ActorSystem("QuickStart")

  val songListParser       = new SongListParser
  val creatorWithChords    = new SongBookWithChordsCreator
  val creatorWithoutChords = new SongBookWithoutChordsCreator

  def retryFlow[T, Mat](flow: Flow[T, T, NotUsed], retries: Int): Flow[T, T, NotUsed] = {
    RestartFlow.withBackoff(
      settings = RestartSettings(
        minBackoff   = 100.milliseconds,
        maxBackoff   = 1.seconds,
        randomFactor = 0.2
      ).withMaxRestarts(4, 1.minute)
    ) { () =>
      {
        scribe.warn("Restarting Flow")
        flow
      }
    }
  }

  def createSongbooks(): Unit = {

    val source:      Source[List[Song], NotUsed]     =
      Source.single(
        songListParser.parse("https://www.ultimate-guitar.com/user/playlist/shared?h=2ObfL4i1q7qG79B6kJPztWy5")
      )
    val flow:        Flow[List[Song], Song, NotUsed] = Flow[List[Song]].mapConcat(identity)
    val detailsFlow: Flow[Song, Song, NotUsed]       = Flow[Song].mapAsync(parallelism = 4)(elem =>
      Future {
        (new SongParser).parse(elem)
      }
    )
    val detailsFlowRetry = retryFlow(detailsFlow, 5)

    val nextFlow:    Flow[Song, List[Song], NotUsed]       = Flow[Song].fold(List.empty[Song])((acc, elem) => acc :+ elem)
    val printTitles: Flow[List[Song], List[Song], NotUsed] = Flow[List[Song]].map(elem => {
      elem.filter(_.lyrics.size<2).foreach(e => scribe.error(e.title, e.author))
//      elem.foreach(e => if (e.lyrics.size < 2) println(e.title))
      elem
    })

    def createSongbook(songBookCreator: SongBookCreator): Flow[List[Song], Unit, NotUsed] =
      Flow[List[Song]].map(songs => songBookCreator.createSongBook(songs))

    val sink: Sink[Unit, NotUsed] = Sink.onComplete {
      case Success(value) =>
        println(value)
        system.terminate()
    }

    val createSongbooksBiFlow = Flow.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
      val bcast = builder.add(Broadcast[List[Song]](2))
      val merge = builder.add(Merge[Unit](2))

      bcast.out(0) ~> createSongbook(creatorWithChords)    ~> merge.in(0)
      bcast.out(1) ~> createSongbook(creatorWithoutChords) ~> merge.in(1)
      FlowShape(bcast.in, merge.out)

    })

    source.via(flow).via(detailsFlowRetry)
      .via(nextFlow)
      //    .via(nextFl)
      .via(printTitles)
      .via(createSongbooksBiFlow)
      //    .via(createSongbook)
      .runWith(sink)
  }
}
