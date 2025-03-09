package parsers

import org.jsoup.Jsoup
import parsers.TekstowoUnrecognizedSongs.tekstowoSearch

class TekstowoParser extends App {

  def processLines(lines: List[String]): List[String] = {
    lines.foldLeft(List.empty[String]) { (acc, line) =>
      acc match {
        case _ if acc.isEmpty                                                                   => acc :+ line
        case _ if acc.size > 1 && line.trim.isEmpty && acc.takeRight(2).forall(_.trim.isEmpty)  => acc
        case _ if acc.size > 1 && line.trim.nonEmpty && acc.takeRight(2).forall(_.trim.isEmpty) => acc.dropRight(1) :+ line
        case _ if acc.size > 1 && line.trim.isEmpty && acc.takeRight(1).forall(_.trim.isEmpty)  => acc :+ line
        case _ if acc.size > 1 && line.trim.nonEmpty && acc.last.trim.isEmpty                   => acc.dropRight(1) :+ line
        case _ if acc.nonEmpty && line.trim.isEmpty && acc.takeRight(1).forall(_.trim.nonEmpty) => acc :+ line
        case _                                                                                  => acc :+ line
      }
    }
  }

  def parseJsoup(url: String): List[String] = {
    val a = Jsoup.connect(url).timeout(1000 * 10).get()
    val lyrics: List[String] = a.select(".song-text").select(".inner-text").get(0).wholeText()
      .replaceAll("\n\r\n", "\n\n")
      .split("\n")
      .toList
    val newL = processLines(lyrics)
//    println(newL)
    newL

  }

  def parse(author: String, title: String): List[String] = {
    val url = TekstowoUnrecognizedSongs.getUrl(author, title)
    try {
      parseJsoup(url)
    } catch {
      case e: Exception =>
        scribe.warn(s"Trying to get alternative link to $title lyrics")
        val url           = s"https://www.tekstowo.pl/szukaj,${tekstowoSearch(author, title)}.html"
        val response      = Jsoup.connect(url).timeout(1000 * 10).get()
        val maybeSongLink = response.select(".flex-group")
        if (maybeSongLink.size() > 0) {
          val songLink = s"https://www.tekstowo.pl${maybeSongLink.get(0).children().get(1).attributes().get("href")}"
          scribe.info(s"Got alternative link to $title lyrics - $songLink")
          val lyrics   = parseJsoup(songLink)
          lyrics
        } else {
          scribe.error(s"Couldn't downolad lyrics for: $author-$title, url: $url - ${e.getMessage}")
          List("ERROR - No TEXT")
        }
    }
  }
}
