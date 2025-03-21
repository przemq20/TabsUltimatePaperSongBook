import flow.MainFlow
import utils.ConfigReader

object Main extends App {
  System.getProperty("os.name") match {
    case os if os.contains("Windows") => System.setProperty("webdriver.gecko.driver", "src\\main\\resources\\geckodriver.exe")
    case "Linux" => System.setProperty("webdriver.gecko.driver", "/usr/local/bin/geckodriver")
  }
  val config = new ConfigReader("TabsUltimate")
  val flow = new MainFlow(config)
  flow.createSongbooks()
}
