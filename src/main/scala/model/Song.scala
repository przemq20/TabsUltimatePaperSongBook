package model

case class Song(
  author:     String,
  title:      String,
  url:        String,
  difficulty: Option[Difficulty] = None,
  key:        Option[Key]        = None,
  capo:       Option[Capo]       = None,
  tuning:     Option[String]     = None,
  text:       List[String]       = Nil,
  lyrics:     Lyrics             = Lyrics(Nil)
) {}
object Song {
  def empty: Song = Song("", "", "")
}
