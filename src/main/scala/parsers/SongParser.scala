package parsers

import database.PostgresConnector
import model.Capo
import model.Difficulty
import model.Key
import model.Song
import org.openqa.selenium.By
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxDriverLogLevel
import org.openqa.selenium.firefox.FirefoxOptions
import scala.jdk.CollectionConverters._

class SongParser(postgresConnector: PostgresConnector) extends Parser[Song] {

  def getAttribute(attributes: List[String], attrName: String): Option[String] = {
    val opt = attributes.find(text => text.contains(attrName)).map(difficulty => difficulty.split(": ").toList)
    if (opt.isDefined && opt.get.size > 1) opt.flatMap(list => list.lift(1)) else None
  }

  def parse(song: Song): Song = {
    if (postgresConnector.songTable.isSonginDB(song)) {
      try {
        scribe.info(s"Song ${song.title} exists in DB")
        val retrievedSong =
          postgresConnector.songTable.getSong(song).head.toSong
        scribe.info(s"[Completed] Song ${song.title} downloaded from DB")
        retrievedSong
      } catch {
        case _: Throwable =>
//          song.copy(lyrics = List("ERROR - lyrics not downloaded from DB"))
          scribe.info(s"Song ${song.title} exists in DB")
          val retrievedSong =
            postgresConnector.songTable.getSong(song).head.toSong
          scribe.info(s"[Completed] Song ${song.title} downloaded from DB")
          retrievedSong
      }
    } else {
      System.setProperty("webdriver.gecko.driver", "/usr/local/bin/geckodriver")
      val options = new FirefoxOptions()
      options.setHeadless(true)
      options.setLogLevel(FirefoxDriverLogLevel.FATAL)
      val driver  = new FirefoxDriver(options)

      driver.manage.window.maximize()

      try {
        scribe.info(s"Downloading info from ${song.title}")

        driver.get(song.url)

        val htmlProductElements = driver.findElements(By.cssSelector(".uTCO3")).asScala.toList.map(_.getText)
        val difficulty          = getAttribute(htmlProductElements, "Difficulty").flatMap(d => Difficulty.fromString(d))
        val title               = song.title
        val tuning              = getAttribute(htmlProductElements, "Tuning")
        val key                 = getAttribute(htmlProductElements, "Key").flatMap(k => Key.fromString(k))
        val capo                = getAttribute(htmlProductElements, "Capo").flatMap(c => Capo.fromString(c))
        val text                = driver.findElement(By.cssSelector(".tK8GG")).getText.split("\n").toList
        val tekstowo            = new TekstowoParser
        val lyrics              = tekstowo.parse(song.author, title)
        scribe.info(s"[COMPLETED] Downloading info from ${song.title}")

        driver.quit()
        val newSong = Song(song.author, title, song.url, difficulty, key, capo, tuning, text, lyrics)
        postgresConnector.songTable.insert(newSong)
        newSong
      } finally {
        driver.quit()
      }
    }
  }
}
