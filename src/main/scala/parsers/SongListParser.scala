package parsers

import database.PostgresConnector
import model.Song
import org.openqa.selenium.By
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxDriverLogLevel
import org.openqa.selenium.firefox.FirefoxOptions

import scala.jdk.CollectionConverters._

class SongListParser(postgresConnector: PostgresConnector) extends Parser[List[Song]] {

  def removeVersionInfo(title: String): String = {
    title.replaceAll("\\s*\\(ver.*?\\)", "").replaceAll("\\s*\\(.*?\\)", "")
  }

  def parse(playlistURL: String): List[Song] = {
    System.setProperty("webdriver.gecko.driver", "/usr/local/bin/geckodriver")
    val options = new FirefoxOptions()
    options.setHeadless(true)
    options.setLogLevel(FirefoxDriverLogLevel.FATAL)
    val driver  = new FirefoxDriver(options)

    driver.manage.window.maximize()
    scribe.info("Downloading songs playlist")
    driver.get(playlistURL)

    val htmlProductElements = driver.findElements(By.cssSelector(".LQUZJ")).asScala.toList

    val elements = htmlProductElements.map(element => element.findElements(By.cssSelector(".lIKMM")).asScala.toList).drop(1)

    val songs: List[Song] = elements.map(element => {
      val author = element.head.getText
      val title  = removeVersionInfo(element(1).getText)
      val url    = element(1).findElements(By.cssSelector(".aPPf7.HT3w5.lBssT")).asScala.toList
        .headOption.map(a => a.getAttribute("href")).get

      Song(author, title, url)
    })
    driver.quit()
    scribe.info(s"Downloading songs playlist completed, playlist contains ${songs.size} songs")

    def songsInDB = postgresConnector.songTable.getSongs
    scribe.info(s"DB contains ${songsInDB.size} songs")
    songs
  }
}
