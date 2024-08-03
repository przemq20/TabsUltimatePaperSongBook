package parsers

import java.util.logging.Level
import model.Song
import org.openqa.selenium.By
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxDriverLogLevel
import org.openqa.selenium.firefox.FirefoxOptions
import scala.jdk.CollectionConverters._

class SongListParser extends Parser[List[Song]] {

  def parse(playlistURL: String): List[Song] = {
    System.setProperty("webdriver.gecko.driver", "/usr/local/bin/geckodriver")
    val options = new FirefoxOptions()
    options.setHeadless(true)
    options.setLogLevel(FirefoxDriverLogLevel.FATAL)
    val driver  = new FirefoxDriver(options)

    driver.manage.window.maximize()
//    driver.setLogLevel(Level.OFF)
    scribe.info("Downloading songs playlist")
    driver.get(playlistURL)

    val htmlProductElements = driver.findElements(By.cssSelector(".LQUZJ")).asScala.toList

    val elements = htmlProductElements.map(element => element.findElements(By.cssSelector(".lIKMM")).asScala.toList).drop(1)
    val songs: List[Song] = elements.map(element =>
      Song(
        element.head.getText,
        element(1).getText,
        element(1).findElements(By.cssSelector(".aPPf7.HT3w5.lBssT")).asScala.toList
          .headOption.map(a => a.getAttribute("href")).get
      )
    ).sortBy(_.title.toLowerCase)
      .sortBy(_.author.toLowerCase)
    scribe.info("Downloading songs playlist completed")

    driver.quit()
    songs
  }
}
