package songBook

import java.io.FileOutputStream
import model.Song
import org.apache.poi.wp.usermodel.HeaderFooterType
import org.apache.poi.xwpf.usermodel._
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr

class SongBookWithChordsCreator extends SongBookCreator {


  def createSongBook(songs: List[Song]): Unit = {
    val document = new XWPFDocument()

    // Set document margins
    setDocumentMargins(document, 720, 720, 720, 720)

    // Title Page
    addTitlePage(document, "Śpiewnik")

    // Empty Page
    document.createParagraph().createRun().addBreak(BreakType.PAGE)

    // Table of Contents
    addTableOfContents(document, songs)

    // Songs
    songs.zipWithIndex.foreach { case (song, index) =>
      addSong(document, song, index + 1)
      if (index < songs.length - 1) document.createParagraph().createRun().addBreak(BreakType.PAGE)
    }

    // Save the document
    val out = new FileOutputStream("spiewniknew.docx")
    document.write(out)
    out.close()
    document.close()
  }

  private def setDocumentMargins(document: XWPFDocument, top: Int, bottom: Int, left: Int, right: Int): Unit = {
    val ctSectPr = document.getDocument.getBody.getSectPr
    if (ctSectPr == null) {
      document.getDocument.getBody.addNewSectPr()
    }
    val sectPr = document.getDocument.getBody.getSectPr
    val pgMar = if (sectPr.isSetPgMar) sectPr.getPgMar else sectPr.addNewPgMar()
    pgMar.setTop(top)
    pgMar.setBottom(bottom)
    pgMar.setLeft(left)
    pgMar.setRight(right)
  }

  private def addTitlePage(document: XWPFDocument, title: String): Unit = {
    val paragraph = document.createParagraph()
    paragraph.setAlignment(ParagraphAlignment.CENTER)
    setParagraphSpacing(paragraph)
    val run = paragraph.createRun()
    run.setText(title)
    run.setBold(true)
    run.setFontSize(24)
    run.addBreak(BreakType.PAGE)
  }

  private def addTableOfContents(document: XWPFDocument, songs: List[Song]): Unit = {
    val paragraph = document.createParagraph()
    paragraph.setAlignment(ParagraphAlignment.CENTER)
    setParagraphSpacing(paragraph)
    val run = paragraph.createRun()
    run.setText("Spis Treści")
    run.setBold(true)
    run.setFontSize(18)
    run.addBreak()

    songs.zipWithIndex.foreach { case (song, index) =>
      val tocParagraph = document.createParagraph()
      tocParagraph.setAlignment(ParagraphAlignment.LEFT)
      setParagraphSpacing(tocParagraph)
      val tocRun = tocParagraph.createRun()
      tocRun.setText(s"${index + 1}. ${song.author} - ${song.title}")
      tocRun.setFontSize(12)
      tocRun.addBreak()
    }
    document.createParagraph().createRun().addBreak(BreakType.PAGE)
  }

  private def addSong(document: XWPFDocument, song: Song, number: Int): Unit = {
    // Song number, author and title
    val headerParagraph = document.createParagraph()
    headerParagraph.setAlignment(ParagraphAlignment.LEFT)
    setParagraphSpacing(headerParagraph)
    val headerRun = headerParagraph.createRun()
    headerRun.setText(s"$number. ${song.author} - ${song.title}")
    headerRun.setBold(true)
    headerRun.setFontSize(14)
    headerRun.addBreak()

    // Difficulty, Key, Capo, Tuning
    song.difficulty.foreach { difficulty =>
      val paragraph = document.createParagraph()
      setParagraphSpacing(paragraph)
      val run = paragraph.createRun()
      run.setText(s"Difficulty: ${difficulty.toString}")
      run.setFontSize(12)
      run.addBreak()
    }
    song.key.foreach { key =>
      val paragraph = document.createParagraph()
      setParagraphSpacing(paragraph)
      val run = paragraph.createRun()
      run.setText(s"Key: ${key.toString}")
      run.setFontSize(12)
      run.addBreak()
    }
    song.capo.foreach { capo =>
      val paragraph = document.createParagraph()
      setParagraphSpacing(paragraph)
      val run = paragraph.createRun()
      run.setText(s"Capo: ${capo.toString}")
      run.setFontSize(12)
      run.addBreak()
    }
    song.tuning.foreach { tuning =>
      val paragraph = document.createParagraph()
      setParagraphSpacing(paragraph)
      val run = paragraph.createRun()
      run.setText(s"Tuning: $tuning")
      run.setFontSize(12)
      run.addBreak()
    }

    // Song text with bold chords
    song.text.foreach { line =>
      val paragraph = document.createParagraph()
      setParagraphSpacing(paragraph)
      val run = paragraph.createRun()
      val parts = line.split(" ")
      parts.foreach { part =>
        if (isChord(part)) {
          run.setBold(true)
          run.setText(part + " ")
          run.setBold(false)
        } else {
          run.setText(part + " ")
        }
      }
      run.addBreak()
    }
  }

  private def setParagraphSpacing(paragraph: XWPFParagraph): Unit = {
    paragraph.setSpacingBetween(1) // Single line spacing
    paragraph.setSpacingBefore(0)
    paragraph.setSpacingAfter(0)
  }

  private def isChord(part: String): Boolean = {
    // Assuming chords are in uppercase letters and may contain additional characters like numbers or slashes
    part.matches("[A-G][#b]?[/\\dA-G]*")
  }
}
