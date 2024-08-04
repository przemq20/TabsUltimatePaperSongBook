package songBook

import java.io.FileOutputStream
import model.Song
import org.apache.poi.wp.usermodel.HeaderFooterType
import org.apache.poi.xwpf.usermodel.BreakType
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.TextAlignment
import org.apache.poi.xwpf.usermodel.XWPFDocument

class SongBookWithoutChordsCreator extends SongBookCreator {
  def createSongBook(songs: List[Song]): Unit = {
    val document = new XWPFDocument()

    def addPageNumbers(doc: XWPFDocument): Unit = {
      val headerFooterPolicy = doc.getHeaderFooterPolicy

      if (headerFooterPolicy == null) {
        doc.createHeaderFooterPolicy()
      }

      val footer    = doc.createFooter(HeaderFooterType.DEFAULT)
      val paragraph = footer.createParagraph()
      paragraph.setAlignment(ParagraphAlignment.CENTER)

      val run = paragraph.createRun()
      run.setText("Page ")
      paragraph.getCTP.addNewFldSimple().setInstr("PAGE \\* MERGEFORMAT")
    }

    val titlePage = document.createParagraph()
    titlePage.setAlignment(ParagraphAlignment.CENTER)
    titlePage.setVerticalAlignment(TextAlignment.CENTER)
    val runTitle  = titlePage.createRun()
    runTitle.setText("Śpiewnik")
    runTitle.setFontSize(36)
    runTitle.setBold(true)
    addPageBreak(document)

    addPageBreak(document)

    val tocPage = document.createParagraph()
    tocPage.setAlignment(ParagraphAlignment.LEFT)
    val runTOC  = tocPage.createRun()
    runTOC.setText("Spis treści")
    runTOC.setFontSize(24)
    runTOC.setBold(true)

    songs.zipWithIndex.foreach {
      case (song, index) =>
        val tocEntry = document.createParagraph()
        tocEntry.setAlignment(ParagraphAlignment.LEFT)
        val runEntry = tocEntry.createRun()
        runEntry.setText(s"${index + 1}. ${song.author} - ${song.title}")
        runEntry.setFontSize(10)
    }
    addPageBreak(document)

    songs.zipWithIndex.foreach { tuple =>
      val song      = tuple._1
      val header    = document.createParagraph()
      header.setAlignment(ParagraphAlignment.CENTER)
      val runHeader = header.createRun()
      runHeader.setText(s"${tuple._2 + 1} - ${song.author} - ${song.title}")
      runHeader.setFontSize(12)
      runHeader.setBold(true)


      song.lyrics.map(l => s"$l\n").foreach { line =>
        val paragraph = document.createParagraph()
        val run       = paragraph.createRun()
        run.setText(line)
        run.setFontSize(10)
      }

      val paragraph = document.createParagraph()
      val run       = paragraph.createRun()
      run.setText("\n")
      run.setFontSize(10)
//      addPageBreak(document)
    }

    val out = new FileOutputStream("spiewnikWithoutChords.docx")
    document.write(out)
    out.close()
    document.close()

    println("Document created successfully!")

  }
  def addPageBreak(document: XWPFDocument): Unit = {
    val paragraph = document.createParagraph()
    val run       = paragraph.createRun()
    run.addBreak(BreakType.PAGE)
  }
}
