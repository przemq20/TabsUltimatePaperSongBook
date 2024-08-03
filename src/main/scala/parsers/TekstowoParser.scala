package parsers

import model.Lyrics
import model.Song
import org.jsoup.Jsoup
import scala.jdk.CollectionConverters._

class TekstowoParser {
  def tekstowoTitle(str: String): String = {
    val replacements = Map(
      'ą' -> 'a',
      'ć' -> 'c',
      'ę' -> 'e',
      'ł' -> 'l',
      'ń' -> 'n',
      'ó' -> 'o',
      'ś' -> 's',
      'ź' -> 'z',
      'ż' -> 'z',
      'Ą' -> 'A',
      'Ć' -> 'C',
      'Ę' -> 'E',
      'Ł' -> 'L',
      'Ń' -> 'N',
      'Ó' -> 'O',
      'Ś' -> 'S',
      'Ź' -> 'Z',
      'Ż' -> 'Z'
    )

    str.map { char =>
      replacements.getOrElse(char, char)
    }.toLowerCase
      .replace(" ", "_")
      .replace("-", "_")
  }

  def parseJsoup(author: String, title: String): Lyrics = {
    val url = (author, title) match {
      case ("a-ha", "Take On Me Acoustic Live") =>
        s"https://www.tekstowo.pl/piosenka,${tekstowoTitle(author)},${tekstowoTitle("Take on me")}.html"
      case (_, "Jolka Jolka Pamiętasz") => "https://www.tekstowo.pl/piosenka,budka_suflera,jolka__jolka_pamietasz_.html"
      case (_, "Wehikuł Czasu") => "https://www.tekstowo.pl/piosenka,dzem,wehikul_czasu_.html"
      case (_, "Im Still Standing") => "https://www.tekstowo.pl/piosenka,elton_john,i_m_still_standing.html"
      case (_, _) => s"https://www.tekstowo.pl/piosenka,${tekstowoTitle(author)},${tekstowoTitle(title)}.html"
    }
    try {
      val a = Jsoup.connect(url).get()
      val lyrics: Lyrics = Lyrics(a.select(".song-text").select(".inner-text").get(0).wholeText()
        .replaceAll("\r", "\n")
        .replace("\n\n", "%%%")
        .replace("\n", "")
        .lines()
        .filter(_.nonEmpty)
        .map(_.replaceAll("%%%{2,}", "%%%%%%"))
        .map(a => a.replaceAll("%%%", "\n"))
        .toList.asScala.toList)
      lyrics
      //    Song(song.author, song.title, song.url, song.difficulty, song.key, song.capo, song.tuning, song.text, lyrics)
    }
    catch {
      case e: Exception =>
        scribe.error(s"Couldn't downolad lyrics for: $author-$title, url: $url")
        Lyrics(Nil)
    }
  }
}
