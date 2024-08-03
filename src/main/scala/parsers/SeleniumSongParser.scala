package parsers

import model.Capo
import model.Difficulty
import model.Key
import model.Song
import org.openqa.selenium.By
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import scala.jdk.CollectionConverters._

class SeleniumSongParser extends Parser[Song] {

  def getAttribute(attributes: List[String], attrName: String): Option[String] = {
    val opt = attributes.find(text => text.contains(attrName)).map(difficulty => difficulty.split(": ").toList)
    if (opt.isDefined && opt.get.size > 1) opt.flatMap(list => list.lift(1)) else None
  }

  def parse(songs: List[Song]): List[Song] = {
    System.setProperty("webdriver.gecko.driver", "/usr/local/bin/geckodriver")
    val options  = new FirefoxOptions()
    options.setHeadless(true)
    val driver   = new FirefoxDriver(options)
    driver.manage.window.maximize()
    val newSongs = songs.map(song => {
      driver.get(song.url)

      val htmlProductElements = driver.findElements(By.cssSelector(".uTCO3")).asScala.toList.map(_.getText)
      val difficulty          = getAttribute(htmlProductElements, "Difficulty").flatMap(d => Difficulty.fromString(d))

      val tuning = getAttribute(htmlProductElements, "Tuning")
      val key    = getAttribute(htmlProductElements, "Key").flatMap(k => Key.fromString(k))
      val capo   = getAttribute(htmlProductElements, "Capo").flatMap(c => Capo.fromString(c))
      println(song.url)
      println(difficulty)
      println(tuning)
      println(key)
      println(capo)
      Song(song.author, song.title, song.url, difficulty, key, capo, tuning)
    })
    driver.quit()
    newSongs
  }
}
