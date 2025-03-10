import flow.CreateSongbookFlow
import utils.ConfigReader

object Main extends App {
  val config = new ConfigReader("TabsUltimate")
  val flow = new CreateSongbookFlow(config)
  flow.createSongbooks()
}
