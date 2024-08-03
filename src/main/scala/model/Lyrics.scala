package model

case class Lyrics(lines: List[String]) {

  def lyricsWithoutChords: Lyrics = {
    val cleanLyrics = lines.map(line =>
      line
        .replaceAll("\\[Chords\\].*?\\[", "[")              // Usuwa sekcję Chords
        .replaceAll("\\[.*?\\]", "")                        // Usuwa znaczniki sekcji
        .replaceAll("\\|.*?\\|", "")                        // Usuwa akordy w formacie | D | D/C# |
        .replaceAll("\\b[A-G][#b]?m?\\d*(?:/\\w+)?\\b", "") // Usuwa akordy w formacie D, D/C#, Bm, G5
        .replaceAll("\n{2,}", "\n")                         // Usuwa puste linie
        .replaceAll("\\[.*?\\]", "")                        // Usuwa znaczniki sekcji
        .replaceAll("\\b[A-G][#b]?m?\\d*(?:/\\w+)?\\b", "") // Usuwa akordy w formacie Am, D/F#, G/B, Gmaj7/B
        .replaceAll("\\s+", " ")                            // Usuwa nadmiarowe spacje
        .replaceAll("(?m)^\\s*\\|.*?\\|\\s*$", "")          // Usuwa akordy w formacie | D | D/C# |
        .replaceAll("(?m)^\\s*$", "") // Usuwa puste linie
    )
    Lyrics(cleanLyrics)
  }
}
