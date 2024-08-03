package SongBook

import model.Capo.NoCapo
import model.Capo.`2ndFret`
import model.Difficulty.Intermediate
import model.Key.Bm
import model.Key.D
import model.Song

object ExampleSongs {
  val exampleSongs: List[Song] = List(
    Song(
      "ABBA",
      "Mamma Mia",
      "https://tabs.ultimate-guitar.com/tab/abba/mamma-mia-chords-52949",
      Some(Intermediate),
      Some(D),
      Some(NoCapo),
      Some("E A D G B E"),
      List(
        "[Intro]",
        "D  Daug  x4",
        " ",
        "[Verse 1]",
        "D         A/D        D                       G",
        "I've been cheated by you",
        "since I don't know when",
        "D    A/D        D                        G",
        "So I made up my mind",
        "it must come to an end",
        "D               Daug",
        "Look at me now",
        "will I ever learn?",
        "D              Daug              G",
        "I don't know how",
        "but I suddenly lose control",
        "                   A",
        "There's a fire within my soul",
        " ",
        "[Pre-chorus]",
        "G    D/F# A",
        "Just one  look and I can hear a bell ring",
        "G   D/F# A",
        "One more look and I forget everything",
        " ",
        "[Chorus]",
        "D",
        "Mamma mia",
        "here I go again",
        "C/G G               D/G",
        "My  my",
        "how can I resist you?",
        "D",
        "Mamma mia",
        "does it show again?",
        "C/G G                      D/G",
        "My  my",
        "just how much I've missed you",
        " ",
        "D              A/C#",
        "Yes",
        "I've been brokenhearted",
        "Bm             F#m/A",
        "Blue since the day we parted",
        "C/G  G   Em         A",
        "Why",
        "why did I ever let you go?",
        "D         Bm",
        "Mamma mia",
        "now I really know,",
        "C/G  G   Em            A",
        "My   my",
        "I could never let you go",
        " ",
        "[Instrumental]",
        "D  Daug  x2",
        " ",
        "[Verse 2]",
        "D         A/D       D                             G",
        "I've been angry and sad about the things that you do",
        "D       A/D           D                              G",
        "I can't count all the times that I've told you we're through",
        "D               Daug",
        "And when you go",
        "when you slam the door",
        "D              Daug                  G",
        "I think you know",
        "that you won't be away too long",
        "                  A",
        "You know that I'm not that strong",
        " ",
        "[Pre-chorus]",
        "G    D/F# A",
        "Just one  look and I can hear a bell ring",
        "G   D/F# A",
        "One more look and I forget everything",
        " ",
        "[Chorus]",
        "D",
        "Mamma mia",
        "here I go again",
        "C/G G               D/G",
        "My  my",
        "how can I resist you?",
        "D",
        "Mamma mia",
        "does it show again?",
        "C/G G                      D/G",
        "My  my",
        "just how much I've missed you",
        " ",
        "D              A/C#",
        "Yes",
        "I've been brokenhearted",
        "Bm             F#m/A",
        "Blue since the day we parted",
        "C/G  G   Em         A",
        "Why",
        "why did I ever let you go?",
        " ",
        "D",
        "Mamma mia",
        "even if I say",
        "C/G G                    D/G",
        "Bye bye",
        "leave me now or never",
        "D",
        "Mamma mia",
        "it's a game we play",
        "C/G G                   D/G",
        "Bye bye doesn't mean forever",
        " ",
        "D",
        "Mamma mia",
        "here I go again",
        "C/G G               D/G",
        "My  my",
        "how can I resist you?",
        "D",
        "Mamma mia",
        "does it show again?",
        "C/G G                      D/G",
        "My  my",
        "just how much I've missed you",
        " ",
        "D              A/C#",
        "Yes",
        "I've been brokenhearted",
        "Bm             F#m/A",
        "Blue since the day we parted",
        "C/G  G   Em         A",
        "Why",
        "why did I ever let you go?",
        "D         Bm",
        "Mamma mia",
        "now I really know,",
        "C/G  G   Em            A",
        "My   my",
        "I could never let you go",
        " ",
        "[Outro]",
        "D  Daug  x5½",
        "(fade out))"
      )
    ),
    Song(
      "a-ha",
      "Take On Me Acoustic Live",
      "https://tabs.ultimate-guitar.com/tab/a-ha/take-on-me-chords-1842621",
      Some(Intermediate),
      Some(Bm),
      Some(`2ndFret`),
      Some("E A D G B E"),
      List(
        "MTV Unplugged version",
        "https://www.youtube.com/watch?v=-xKM3mGt2pE",
        " ",
        "[Intro]",
        "Am   D/F#   G   C G/B",
        "Am   D/F#   G   C Gmaj7/B",
        " ",
        "[Verse 1]",
        "Am         D/F#",
        "Talking away",
        "      Em",
        "Well",
        "I don't know what I'm to say",
        "     Am          D/F#",
        "I'll say it anyway",
        "  Em",
        "Today's another day to find you",
        "Am         D/F#",
        "  Shying away",
        "Em                        C",
        "  I'll be coming for your love",
        "ok?",
        " ",
        "[Chorus]",
        "G      D    Em     D/F#",
        "Take   on   me",
        "G      Bm   Em   C",
        "Take   me   on",
        "G      B7   Em   C",
        "I'll   be   gone",
        "           G   D/F#   C   D",
        "In a day or two...",
        " ",
        "[Verse 2]",
        "   Am           D/F#",
        "So needless to say",
        "    Em",
        "I'm odds and ends",
        "but that's me,",
        "    Am            D/F#",
        "I'm stumbling away",
        "Em",
        "Slowly learning that life is ok and",
        "Am           D/F#",
        "  Say after me",
        "Em                    C",
        " It's no better to be safe than sorry and",
        " ",
        "[Chorus]",
        "G      D    Em     D/F#",
        " Take  on   me",
        "G      Bm    Em   C",
        "Take   me   on",
        "G      B7   Em   C",
        "I'll   be   gone",
        "           G    D/F#   C   D",
        "In a day or two...",
        " ",
        "[Bridge]",
        "Am   D   G   C G/B",
        "Am   D   G   C Gmaj7/B",
        "Am   D   Am   D",
        " ",
        "[Verse 3]",
        "        Am                 D/F#",
        "And all things that you say",
        "Em",
        "Is it life or just to play",
        "   Am          D/F#",
        "My worries away?",
        "       Em",
        "You're all the things I've got to remember",
        "      Am        D/F#",
        "You're shying away",
        "Em                     C",
        "  I'll be coming for you anyway",
        " ",
        "[Chorus]",
        "G      D    Em     D/F#",
        "Take   on   me",
        "G      Bm   Em   C",
        "Take   me   on",
        "G      B7   Em   C",
        "I'll   be   gone",
        "           G    Bm   Em   C",
        "In a day or two...",
        " ",
        "[Outro]",
        "G      B7   Em   C",
        "I'll   be   gone",
        "           G    D/F#   Em   C",
        "In a day or two...",
        "           G    D/F#   C",
        "In a day or two..."
      )
    )
  )

}
