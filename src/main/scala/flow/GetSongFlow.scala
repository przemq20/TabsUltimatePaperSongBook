package flow

import database.PostgresConnector
import database.model.SongDBModel
import model.Song
import org.apache.pekko.NotUsed
import org.apache.pekko.stream.FlowShape
import org.apache.pekko.stream.RestartSettings
import org.apache.pekko.stream.scaladsl.Broadcast
import org.apache.pekko.stream.scaladsl.Flow
import org.apache.pekko.stream.scaladsl.GraphDSL
import org.apache.pekko.stream.scaladsl.GraphDSL.Implicits._
import org.apache.pekko.stream.scaladsl.Merge
import org.apache.pekko.stream.scaladsl.RestartFlow
import org.postgresql.util.PSQLException
import parsers.SongParser
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class GetSongFlow(postgresConnector: PostgresConnector) {

  private def retryFlow[T, Mat](flow: Flow[T, T, NotUsed], retries: Int): Flow[T, T, NotUsed] = {
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

  private def downloadSongsFromDB: Flow[List[Song], List[Song], NotUsed] = {
    val songsInDb = postgresConnector.songTable.getSongs
    Flow[List[Song]].map(songs =>
      songs.flatMap(song => songsInDb.find(SongDBModel.generateSongId(_) == SongDBModel.generateSongId(song)))
    )
  }

  private val getNotDownloadedSongsFlow: Flow[List[Song], List[Song], NotUsed] = {
    val songsInDB = postgresConnector.songTable.getSongs
    Flow[List[Song]].map(songs =>
      songs.filterNot(song => songsInDB.map(s => SongDBModel.generateSongId(s)).contains(SongDBModel.generateSongId(song)))
    )
  }

  private val getDownloadedSongsFlow: Flow[List[Song], List[Song], NotUsed] = {
    val songsInDB = postgresConnector.songTable.getSongs
    Flow[List[Song]].map(songs =>
      songs.filter(song => songsInDB.map(s => SongDBModel.generateSongId(s)).contains(SongDBModel.generateSongId(song)))
    )
  }

  private val printSongsToDownloadFlow: Flow[List[Song], List[Song], NotUsed] = {
    Flow[List[Song]].map(songs => {
      songs.foreach(song => scribe.info(s"To download: ${song.title} ${song.author}"))
      songs
    })
  }

  private val splitListIntoSongsFlow: Flow[List[Song], Song, NotUsed] =
    Flow[List[Song]].mapConcat(identity)

  private val downloadSongDetailsFlow: Flow[Song, Song, NotUsed] =
    Flow[Song].mapAsync(parallelism = 4)(elem =>
      Future {
        new SongParser(postgresConnector).parse(elem)
      }.recoverWith {
        case _: PSQLException => Future {
            new SongParser(postgresConnector).parse(elem)
          }
      }
    )

  private val detailsFlowRetry: Flow[Song, Song, NotUsed] = retryFlow(downloadSongDetailsFlow, 5)

  val getSongsFlow: Flow[List[Song], Song, NotUsed] = Flow.fromGraph(GraphDSL.create() {
    implicit builder: GraphDSL.Builder[NotUsed] =>
      val bcast = builder.add(Broadcast[List[Song]](2))
      val merge = builder.add(Merge[Song](2))

      bcast.out(0)             ~>
        getDownloadedSongsFlow ~>
        downloadSongsFromDB    ~>
        splitListIntoSongsFlow ~>
        merge.in(0)

      bcast.out(1)                ~>
        getNotDownloadedSongsFlow ~>
        printSongsToDownloadFlow  ~>
        splitListIntoSongsFlow    ~>
        detailsFlowRetry          ~>
        merge.in(1)

      FlowShape(bcast.in, merge.out)
  })
}
