package parsers

import model.Song
import org.jsoup.Jsoup
import scala.jdk.CollectionConverters._

class TekstowoParser extends App {
//  parseJsoup("a","Kilku Kumpli Weź")
//  parseJsoup("Ed Sheeran", "I see fire")
//  parseJsoup("Ed Sheeran", "Im Still Standing")

  def processLines(lines: List[String]): List[String] = {
    lines.foldLeft(List.empty[String]) { (acc, line) =>
      acc match {
        case _ if acc.isEmpty                                                                   => acc :+ line
        case _ if acc.size > 1 && line.trim.isEmpty && acc.takeRight(2).forall(_.trim.isEmpty)  => acc
        case _ if acc.size > 1 && line.trim.nonEmpty && acc.takeRight(2).forall(_.trim.isEmpty) => acc.dropRight(1) :+ line
        case _ if acc.size > 1 && line.trim.isEmpty && acc.takeRight(1).forall(_.trim.isEmpty)  => acc :+ line
        case _ if acc.size > 1 && line.trim.nonEmpty && acc.last.trim.isEmpty                   => acc.dropRight(1) :+ line
        case _ if acc.size > 0 && line.trim.isEmpty && acc.takeRight(1).forall(_.trim.nonEmpty) => acc :+ line
        case _                                                                                  =>
          println(line)
          acc :+ line
      }
    }
  }

  def parseJsoup(author: String, title: String): List[String] = {
    val url = TekstowoUnrecognizedSongs.getUrl(author, title)

    try {
      val a = Jsoup.connect(url).timeout(1000 * 10).get()
      val lyrics: List[String] = a.select(".song-text").select(".inner-text").get(0).wholeText()
        .replaceAll("\n\r\n", "\n\n")
        .split("\n")
        .toList
//      lyrics.zipWithIndex.foreach(a => println(s"${a._2} ${a._1}"))

      val newL = processLines(lyrics)
      newL.zipWithIndex.foreach(a => println(s"${a._2} ${a._1}"))
      lyrics
      //    Song(song.author, song.title, song.url, song.difficulty, song.key, song.capo, song.tuning, song.text, lyrics)
    } catch {
      case e: Exception =>
        scribe.error(s"Couldn't downolad lyrics for: $author-$title, url: $url - ${e.getMessage}")
        List("ERROR - No TEXT")
    }
  }
}
