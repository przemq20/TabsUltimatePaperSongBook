package parsers

import model.Capo
import model.Difficulty
import model.Key
import model.Lyrics
import model.Song
import org.openqa.selenium.By
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxDriverLogLevel
import org.openqa.selenium.firefox.FirefoxOptions

import java.util.logging.Level
import scala.jdk.CollectionConverters._

class SongParser extends Parser[Song] {

  def getAttribute(attributes: List[String], attrName: String): Option[String] = {
    val opt = attributes.find(text => text.contains(attrName)).map(difficulty => difficulty.split(": ").toList)
    if (opt.isDefined && opt.get.size > 1) opt.flatMap(list => list.lift(1)) else None
  }

  def removeVersionInfo(title: String): String = {
    title.replaceAll("\\s*\\(ver.*?\\)", "").replaceAll("\\s*\\(.*?\\)", "")
  }

  def parse(song: Song): Song = {
    System.setProperty("webdriver.gecko.driver", "/usr/local/bin/geckodriver")
//    System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true")
    System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null")
    val options = new FirefoxOptions()
    options.setHeadless(true)
    options.setLogLevel(FirefoxDriverLogLevel.fromLevel(Level.OFF))

    val driver = new FirefoxDriver(options)
//    driver.setLogLevel(Level.OFF)
    driver.manage.window.maximize()
    scribe.info(s"Downloading info from ${song.title}")

    driver.get(song.url)

    val htmlProductElements = driver.findElements(By.cssSelector(".uTCO3")).asScala.toList.map(_.getText)
    val difficulty          = getAttribute(htmlProductElements, "Difficulty").flatMap(d => Difficulty.fromString(d))
    val title               = removeVersionInfo(song.title)
    val tuning              = getAttribute(htmlProductElements, "Tuning")
    val key                 = getAttribute(htmlProductElements, "Key").flatMap(k => Key.fromString(k))
    val capo                = getAttribute(htmlProductElements, "Capo").flatMap(c => Capo.fromString(c))
    val text                = driver.findElement(By.cssSelector(".tK8GG")).getText.split("\n").toList
    val tekstowo            = new TekstowoParser
    val lyrics              = tekstowo.parseJsoup(song.author, title)
    scribe.info(s"Downloading info from ${song.title} completed")

    driver.quit()
    Song(song.author, title, song.url, difficulty, key, capo, tuning, text, lyrics)
  }
}
