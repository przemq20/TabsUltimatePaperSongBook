package flow

import database.PostgresConnector
import database.model.SongDBModel
import java.text.Collator
import java.util.Locale
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
import org.postgresql.util.PSQLException
import parsers.SongListParser
import parsers.SongParser
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.Failure
import scala.util.Success
import songBook.SongBookCreator
import songBook.SongBookWithChordsCreator
import songBook.SongBookWithoutChordsCreator
import utils.ConfigReader

class CreateSongbookFlow(config: ConfigReader) {
  implicit val system: ActorSystem = ActorSystem("TabsUltimate")

  final val playlistURL = config.getVariableString("playlistURL")

  val postgresConnector    = new PostgresConnector
  val songListParser       = new SongListParser(postgresConnector)
  val creatorWithChords    = new SongBookWithChordsCreator
  val creatorWithoutChords = new SongBookWithoutChordsCreator

  def retryFlow[T, Mat](flow: Flow[T, T, NotUsed], retries: Int): Flow[T, T, NotUsed] = {
    RestartFlow.onFailuresWithBackoff(
      settings = RestartSettings(
        minBackoff   = 100.milliseconds,
        maxBackoff   = 10.seconds,
        randomFactor = 0.2
      ).withMaxRestarts(retries, 1.seconds)
    ) { () =>
      {
        scribe.warn("Restarting Flow")
        flow
      }
    }
  }

  def createSongbooks(): Unit = {

    val downloadSongListSource: Source[List[Song], NotUsed] =
      Source.single(
        songListParser.parse(playlistURL)
      )

    val getNotDownloadedSongsFlow: Flow[List[Song], List[Song], NotUsed] = {
      val songsInDB = postgresConnector.songTable.getSongs
      Flow[List[Song]].map(songs =>
        songs.filterNot(song => songsInDB.map(s => SongDBModel.generateSongId(s)).contains(SongDBModel.generateSongId(song)))
      )
    }

    val printSongsToDownloadFlow: Flow[List[Song], List[Song], NotUsed] = {
      Flow[List[Song]].map(songs => {
        songs.foreach(song => scribe.info(s"To download: ${song.title} ${song.author}"))
        songs
      })
    }

    val splitListIntoSongsFlow: Flow[List[Song], Song, NotUsed] =
      Flow[List[Song]].mapConcat(identity)

    val downloadSongDetailsFlow: Flow[Song, Song, NotUsed] =
      Flow[Song].mapAsync(parallelism = 4)(elem =>
        Future {
          new SongParser(postgresConnector).parse(elem)
        }.recoverWith {
          case e: PSQLException => Future {
              new SongParser(postgresConnector).parse(elem)
            }
        }
      )

    val detailsFlowRetry = retryFlow(downloadSongDetailsFlow, 5)

    val concatenateSongs: Flow[Song, List[Song], NotUsed] =
      Flow[Song].fold(List.empty[Song])((acc, elem) => acc :+ elem)

    val printTitlesAndSort: Flow[List[Song], List[Song], NotUsed] =
      Flow[List[Song]].map(elem => {
        scribe.info(s"Retrieved ${elem.size} songs")
        elem.filter(_.lyrics.size < 2).foreach(e => scribe.error(s"${e.title}, ${e.author}"))
        val collator:              Collator       = Collator.getInstance(new Locale.Builder().setLanguage("pl").setRegion("PL").build)
        implicit val songOrdering: Ordering[Song] = Ordering.by { song: Song =>
          (collator.getCollationKey(song.author), collator.getCollationKey(song.title))
        }
        elem.sorted
      })

    def createSongbook(songBookCreator: SongBookCreator): Flow[List[Song], Unit, NotUsed] =
      Flow[List[Song]].map(songs => songBookCreator.createSongBook(songs))

    val sink: Sink[Unit, NotUsed] = Sink.onComplete {
      case Success(value)     =>
        println(value)
        system.terminate()
      case Failure(exception) =>
        exception.printStackTrace()
        system.terminate()
    }

    val createSongbooksBiFlow = Flow.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
      val bcast = builder.add(Broadcast[List[Song]](2))
      val merge = builder.add(Merge[Unit](2))

      bcast.out(0) ~> createSongbook(creatorWithChords)    ~> merge.in(0)
      bcast.out(1) ~> createSongbook(creatorWithoutChords) ~> merge.in(1)
      FlowShape(bcast.in, merge.out)

    })

    downloadSongListSource
      //      .via(getNotDownloadedSongsFlow)
      //      .via(printSongsToDownloadFlow)
      .via(splitListIntoSongsFlow)
      .via(detailsFlowRetry)
      .via(concatenateSongs)
      .via(printTitlesAndSort)
      .via(createSongbooksBiFlow)
      .runWith(sink)
  }
}
