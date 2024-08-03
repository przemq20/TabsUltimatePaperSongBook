package parsers

object TekstowoUnrecognizedSongs {

  val replacements = Map(
    'ą' -> 'a',
    'ć' -> 'c',
    'ę' -> 'e',
    'ł' -> 'l',
    'ń' -> 'n',
    'ó' -> 'o',
    'ś' -> 's',
    'ź' -> 'z',
    'ż' -> 'z',
    'Ą' -> 'A',
    'Ć' -> 'C',
    'Ę' -> 'E',
    'Ł' -> 'L',
    'Ń' -> 'N',
    'Ó' -> 'O',
    'Ś' -> 'S',
    'Ź' -> 'Z',
    'Ż' -> 'Z'
  )

  def getUrl(author: String, title: String): String = {
    (author, title) match {
      case ("Misc Traditional", "Bella Ciao")                                                         =>
        "https://www.tekstowo.pl/piosenka,bella_ciao,bella_ciao.html"
      case ("Misc Traditional", "Morskie Opowieści")                                                  =>
        "https://www.tekstowo.pl/piosenka,szanty,morskie_opowiesci.html"
      case ("Misc Cartoons", "Fineasz I Ferb - Theme")                                                =>
        "https://www.tekstowo.pl/piosenka,fineasz_i_ferb,czolowka.html"
      case ("Misc Traditional", "Bitwa")                                                              =>
        "https://www.tekstowo.pl/piosenka,mechanicy_szanty,bitwa.html"
      case ("Myslovitz", "Nigdy Nie Znajdziesz Sobie Przyjaciół Jeśli Nie Będziesz Taki Jak Wszyscy") =>
        "https://www.tekstowo.pl/piosenka,myslovitz,nigdy_nie_znajdziesz_sobie_przyjaciol__.html"
      case ("Taylor Swift", "Love Story - You Belong With Me Live")                                   =>
        "https://www.tekstowo.pl/piosenka,taylor_swift,love_story.html"
      case ("Edyta Górniak and Mieczysław Szcześniak", "Dumka Na Dwa Serca")                          =>
        "https://www.tekstowo.pl/piosenka,edyta_gorniak,dumka_na_dwa_serca.html"
      case ("Męskie Granie Orkiestra 2021", "Nikt Tak Pięknie Nie Mówił Że Się Boi Miłości")          =>
        "https://www.tekstowo.pl/piosenka,daria_zawialow__dawid_podsiadlo,nikt_tak_pieknie_nie_mowil__ze_sie_boi_milosci.html"
      case ("Misc Cartoons", "Vaiana - Drobnostka")                                                   =>
        "https://www.tekstowo.pl/piosenka,igor_kwiatkowski,drobnostka.html"

      case (_, _) => s"https://www.tekstowo.pl/piosenka,${tekstowoTitle(author)},${tekstowoTitle(title)}.html"
    }

  }

  def tekstowoTitle(str: String): String = {
    str.map { char =>
      replacements.getOrElse(char, char)
    }.toLowerCase
      .replace(" ", "_")
      .replace("-", "_")
  }

  def tekstowoSearch(author: String, title: String): String = {
    s"$author+$title".map { char =>
      replacements.getOrElse(char, char)
    }.toLowerCase
      .replace(" ", "+")
      .replace("-", "+")
  }

}
