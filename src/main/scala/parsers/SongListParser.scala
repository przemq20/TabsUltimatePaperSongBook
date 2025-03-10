package parsers

import database.PostgresConnector
import model.Song
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxDriverLogLevel
import org.openqa.selenium.firefox.FirefoxOptions
import scala.jdk.CollectionConverters._

class SongListParser(postgresConnector: PostgresConnector) extends Parser[List[Song]] {

  def removeVersionInfo(title: String): String = {
    title.replaceAll("\\s*\\(ver.*?\\)", "").replaceAll("\\s*\\(.*?\\)", "")
  }

  def parse(playlistURL: String): List[Song] = {
    val options = new FirefoxOptions()
    options.setHeadless(true)
    options.setLogLevel(FirefoxDriverLogLevel.FATAL)
    val driver  = new FirefoxDriver(options)

    driver.manage.window.maximize()
    scribe.info("Downloading songs playlist")
    driver.get(playlistURL)

    val htmlProductElements = driver.findElements(By.cssSelector(".dyhP1")).asScala.toList

    val elements = htmlProductElements.map(element => element.findElements(By.cssSelector(".qNp1Q")).asScala.toList).drop(1)

    val songs: List[Song] = elements.map(element => {
      val author = element.head.getText match {
        case mgo if mgo.contains("Męskie Granie") => "Męskie Granie Orkiestra"
        case other if other.contains("Misc")      => "Inne"
        case feat if feat.contains("feat")        => feat.split("feat").head
        case song @ _                             => song
      }
      val title  = removeVersionInfo(element(1).getText)
      val url    = element(1).findElement(By.cssSelector(".WfRYb")).getAttribute("href")

      Song(author, title, url)
    })
    driver.quit()
    scribe.info(s"Downloading songs playlist completed, playlist contains ${songs.size} songs")

    val songsInDB = postgresConnector.songTable.getSongs
    scribe.info(s"DB contains ${songsInDB.size} songs")
//    scribe.info(s"diff: ${songs.diff(songsInDB).size}" )
    songs
  }
}
