import SongBook.SongBookCreator
import parsers.SongListParser
import parsers.SongParser

object Main extends App {
  val songListParser = new SongListParser
  val songParser     = new SongParser
  val creator        = new SongBookCreator

  val songs =
    songListParser.parse("https://www.ultimate-guitar.com/user/playlist/shared?h=2ObfL4i1q7qG79B6kJPztWy5")

  songs.foreach(println)
  val songsWithText = songs.map(song => {
    val songWithLyrics = songParser.parse(song)
    songWithLyrics
  })

  creator.createSongBook(songsWithText)
}
