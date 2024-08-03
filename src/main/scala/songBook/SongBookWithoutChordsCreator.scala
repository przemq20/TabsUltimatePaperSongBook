package songBook

import model.Song
import org.apache.poi.wp.usermodel.HeaderFooterType
import org.apache.poi.xwpf.usermodel.{BreakType, ParagraphAlignment, TextAlignment, XWPFDocument}
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr

import java.io.FileOutputStream

class SongBookWithoutChordsCreator extends SongBookCreator {
  //  val songs = ExampleSongs.exampleSongs
  def createSongBook(songs: List[Song]): Unit = {
    // Tworzenie nowego dokumentu
    val document = new XWPFDocument()

    // Funkcja do dodania numerów stron w stopce
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

    // Ustawienie marginesów dla stron parzystych i nieparzystych
    def setPageMargins(section: CTSectPr, leftEven: Int, rightEven: Int, leftOdd: Int, rightOdd: Int): Unit = {
      val pageMar   = section.addNewPgMar()
      pageMar.setLeft(leftOdd)
      pageMar.setRight(rightOdd)
      pageMar.setTop(1440)
      pageMar.setBottom(1440)
      addPageNumbers(document)
      val sectPr    = document.getDocument.getBody.getSectPr
      val pgMarEven = sectPr.addNewPgMar()
      pgMarEven.setLeft(leftEven)
      pgMarEven.setRight(rightEven)
      pgMarEven.setTop(1440)
      pgMarEven.setBottom(1440)
    }

    // Dodanie pierwszej sekcji z marginesami
    val sectPr = document.getDocument.getBody.addNewSectPr()
    setPageMargins(sectPr, 720, 1440, 1440, 720)

    // Dodanie strony tytułowej
    val titlePage = document.createParagraph()
    titlePage.setAlignment(ParagraphAlignment.CENTER)
    titlePage.setVerticalAlignment(TextAlignment.CENTER)
    val runTitle  = titlePage.createRun()
    runTitle.setText("Śpiewnik")
    runTitle.setFontSize(36)
    runTitle.setBold(true)
    addPageBreak(document)

    // Dodanie pustej strony
    addPageBreak(document)

    // Dodanie spisu treści
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
        runEntry.setFontSize(12)
    }
    addPageBreak(document)

    // Dodanie piosenek
    songs.zipWithIndex.foreach { tuple =>
      val song      = tuple._1
      val header    = document.createParagraph()
      header.setAlignment(ParagraphAlignment.CENTER)
      val runHeader = header.createRun()
      runHeader.setText(s"${tuple._2 + 1} - ${song.author} - ${song.title}")
      runHeader.setFontSize(20)
      runHeader.setBold(true)

//      val info    = document.createParagraph()
//      info.setAlignment(ParagraphAlignment.LEFT)
//      val runInfo = info.createRun()
//      runInfo.setText(
//        s"""Trudność: ${song.difficulty.map(_.toString).getOrElse("N/A")}
//           |Klucz: ${song.key.map(_.toString).getOrElse("N/A")}
//           |Capo: ${song.capo.map(_.toString).getOrElse("N/A")}
//           |Strojenie: ${song.tuning.getOrElse("N/A")}
//       """.stripMargin
//      )
//      runInfo.setFontSize(11)

      // Podziel tekst na dwie kolumny
      val textTable = document.createTable()
      textTable.removeBorders()

      val tableRow = textTable.createRow()
      val column1  = tableRow.getCell(0)
      val column2  = tableRow.addNewTableCell()

      val halfTextLength = (song.lyrics.length + 1) / 2
      song.lyrics.map(a => s"$a\n").splitAt(halfTextLength) match {
        case (firstHalf, secondHalf) =>
          firstHalf.foreach { line =>
            val para = column1.addParagraph()
            para.setSpacingBetween(1.0)
            val run  = para.createRun()
            run.setText(line)
            run.setFontSize(12)
          }
          secondHalf.foreach { line =>
            val para = column2.addParagraph()
            val run  = para.createRun()
            run.setText(line)
            run.setFontSize(11)
          }
      }

      addPageBreak(document)
    }

    // Zapisywanie dokumentu do pliku
    val out = new FileOutputStream("spiewnikWithoutChords.docx")
    document.write(out)
    out.close()
    document.close()

    println("Document created successfully!")

    // Funkcja dodająca przerwę stronicową do dokumentu

  }
  def addPageBreak(document: XWPFDocument): Unit = {
    val paragraph = document.createParagraph()
    val run       = paragraph.createRun()
    run.addBreak(BreakType.PAGE)
  }
}
