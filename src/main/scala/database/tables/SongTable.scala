package database.tables
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import database.model.SongDBModel
import database.model.SongDBModel.generateSongId
import doobie.implicits._
import doobie.util.transactor
import model.Song
import scala.concurrent.duration.DurationInt

class SongTable(xa: transactor.Transactor.Aux[IO, Unit]) {
  private val dropQuery =
    sql"""
    DROP TABLE IF EXISTS songs;
  """.update.run

  private val createQuery =
    sql"""
    CREATE TABLE IF NOT EXISTS songs (
      ID         VARCHAR UNIQUE,
      author     VARCHAR,
      title      VARCHAR,
      url        VARCHAR,
      difficulty VARCHAR,
      key        VARCHAR,
      capo       VARCHAR,
      tuning     VARCHAR,
      text       VARCHAR,
      lyrics     VARCHAR,
      PRIMARY KEY (ID)
    )
  """.update.run

  def create(): Unit = {
    createQuery.transact(xa).unsafeRunSync()
  }

  def drop(): Unit = {
    dropQuery.transact(xa).unsafeRunSync()
  }

  def isSonginDB(song: Song): Boolean = {
    val maybeSonginDB = getSong(song)
    maybeSonginDB.nonEmpty
  }

  def insert(song: Song): Unit = {
    if (!isSonginDB(song)) {
      val songDbModel = SongDBModel.fromSong(song)
      scribe.info(s"Trying to insert ${song.title} into DB")
      sql"""insert into songs (ID, author, title, url, difficulty, key, capo, tuning, text, lyrics)
           values (${songDbModel.ID}, ${songDbModel.author}, ${songDbModel.title}, ${songDbModel.url},
        ${songDbModel.difficulty}, ${songDbModel.key}, ${songDbModel.capo},
        ${songDbModel.tuning}, ${songDbModel.text}, ${songDbModel.lyrics})"""
        .update.run
        .transact(xa)
        .unsafeRunSync()
      scribe.info(s"[COMPLETED] Trying to insert ${song.title} into DB")
    }
  }

  def getSongs: List[Song] = {
    val songsInDB = sql"select * from songs"
      .query[SongDBModel]
      .to[List]
      .transact(xa)
      .timeout(1.minute)
      .unsafeRunSync()

    songsInDB.map(_.toSong)
  }

  def getSong(song: Song): List[SongDBModel] = {
    val id = generateSongId(song)
    sql"select * from songs WHERE ID = ${id}"
      .query[SongDBModel]
      .to[List]
      .transact(xa)
      .timeout(1.minute)
      .unsafeRunSync()
  }

}
