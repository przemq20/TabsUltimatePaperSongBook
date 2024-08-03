package songBook

import model.Song

trait SongBookCreator {
  def createSongBook(list: List[Song]): Unit
}
