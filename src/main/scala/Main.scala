import flow.CreateSongbookFlow

object Main extends App {
  System.getProperty("os.name") match {
    case os if os.contains("Windows") => System.setProperty("webdriver.gecko.driver", "src\\main\\resources\\geckodriver.exe")
    case "Linux" => System.setProperty("webdriver.gecko.driver", "/usr/local/bin/geckodriver")
  }
  val flow = new CreateSongbookFlow
  flow.createSongbooks()
}
