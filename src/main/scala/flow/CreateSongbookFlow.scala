package flow

import database.PostgresConnector
import model.Song
import org.apache.pekko.NotUsed
import org.apache.pekko.stream.FlowShape
import org.apache.pekko.stream.scaladsl.Broadcast
import org.apache.pekko.stream.scaladsl.Flow
import org.apache.pekko.stream.scaladsl.GraphDSL
import org.apache.pekko.stream.scaladsl.GraphDSL.Implicits._
import org.apache.pekko.stream.scaladsl.Merge
import songBook.SongBookCreator
import songBook.SongBookWithChordsCreator
import songBook.SongBookWithoutChordsCreator

class CreateSongbookFlow {
  private val creatorWithChords    = new SongBookWithChordsCreator
  private val creatorWithoutChords = new SongBookWithoutChordsCreator

  private def createSongbook(songBookCreator: SongBookCreator): Flow[List[Song], Unit, NotUsed] =
    Flow[List[Song]].map(songs => songBookCreator.createSongBook(songs))

  val createSongbooksBiFlow: Flow[List[Song], Unit, NotUsed] = Flow.fromGraph(GraphDSL.create() {
    implicit builder: GraphDSL.Builder[NotUsed] =>
      val bcast = builder.add(Broadcast[List[Song]](2))
      val merge = builder.add(Merge[Unit](2))

      bcast.out(0)                           ~>
        createSongbook(creatorWithChords)    ~>
        merge.in(0)
      bcast.out(1)                           ~>
        createSongbook(creatorWithoutChords) ~>
        merge.in(1)

      FlowShape(bcast.in, merge.out)
  })
}
