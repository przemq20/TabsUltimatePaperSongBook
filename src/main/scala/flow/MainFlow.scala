package flow

import database.PostgresConnector
import java.text.Collator
import java.util.Locale
import model.Song
import org.apache.pekko.NotUsed
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.scaladsl.Flow
import org.apache.pekko.stream.scaladsl.Sink
import org.apache.pekko.stream.scaladsl.Source
import parsers.SongListParser
import scala.util.Failure
import scala.util.Success
import utils.ConfigReader

class MainFlow(config: ConfigReader) {
  implicit val system: ActorSystem = ActorSystem("TabsUltimate")

  final val playlistURL = config.getVariableString("playlistURL")

  val postgresConnector = new PostgresConnector
  private val songListParser    = new SongListParser(postgresConnector)

  def createSongbooks(): Unit = {

    val downloadSongListSource: Source[List[Song], NotUsed] =
      Source.single(
        songListParser.parse(playlistURL)
      )

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

    val sink: Sink[Unit, NotUsed] = Sink.onComplete {
      case Success(value)     =>
        println(value)
        system.terminate()
      case Failure(exception) =>
        exception.printStackTrace()
        system.terminate()
    }

    val getSongsFlow       = new GetSongFlow(postgresConnector).getSongsFlow
    val createSongbookFlow = new CreateSongbookFlow

    downloadSongListSource
      .via(getSongsFlow)
      .via(concatenateSongs)
      .via(printTitlesAndSort)
      .via(createSongbookFlow.createSongbooksBiFlow)
      .runWith(sink)
  }
}
