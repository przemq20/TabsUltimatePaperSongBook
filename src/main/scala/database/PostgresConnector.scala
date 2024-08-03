package database

import cats.effect.IO
import database.PostgresConnector.PASSWORD
import database.PostgresConnector.URL
import database.PostgresConnector.USERNAME
import database.tables.SongTable
import doobie.Transactor
import utils.ConfigReader

class PostgresConnector {
  private val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    URL,
    USERNAME,
    PASSWORD,
    None
  )

  val songTable = new SongTable(xa)
}
object PostgresConnector {
  val config = new ConfigReader("TabsUltimate.PostgresSql.credentials")
  final lazy val URL:      String = config.getVariableString("URL")
  final lazy val USERNAME: String = config.getVariableString("user")
  final lazy val PASSWORD: String = config.getVariableString("pass")
}
