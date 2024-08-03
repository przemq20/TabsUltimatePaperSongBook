package database.model

import java.math.BigInteger
import java.security.MessageDigest
import model.Capo
import model.Difficulty
import model.Key
import model.Song
case class SongDBModel(
  ID:         String,
  author:     String,
  title:      String,
  url:        String,
  difficulty: String = "",
  key:        String = "",
  capo:       String = "",
  tuning:     String = "",
  text:       String = "",
  lyrics:     String = ""
) {
  def toSong: Song = {
    Song(
      author     = author,
      title      = title,
      url        = url,
      difficulty = Difficulty.fromString(difficulty),
      key        = Key.fromString(key),
      capo       = Capo.fromString(capo),
      tuning     = if (tuning.nonEmpty) Some(tuning) else None,
      text       = text.split("\n").toList,
      lyrics     = lyrics.split("\n").toList
    )
  }
}
object SongDBModel {
  def generateSongId(song: Song): String = {
    val combinedString = s"${song.author}-${song.title}"
    val md             = MessageDigest.getInstance("SHA-256")
    val digest         = md.digest(combinedString.getBytes)
    val bigInt         = new BigInteger(1, digest)
    bigInt.toString(16)
  }

  def fromSong(song: Song): SongDBModel = {
    SongDBModel(
      ID         = generateSongId(song),
      author     = song.author,
      title      = song.title,
      url        = song.url,
      difficulty = if (song.difficulty.isDefined) song.difficulty.get.toString else "",
      key        = if (song.key.isDefined) song.key.get.toString else "",
      capo       = if (song.capo.isDefined) song.capo.get.toString else "",
      tuning     = if (song.tuning.isDefined) song.tuning.get else "",
      text       = song.text.mkString("\n"),
      lyrics     = song.lyrics.mkString("\n")
    )
  }
}
