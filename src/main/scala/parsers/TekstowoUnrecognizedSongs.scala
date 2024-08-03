package parsers

object TekstowoUnrecognizedSongs {
  def getUrl(author: String, title: String): String = {
    (author, title) match {
      case ("a-ha", "Take On Me Acoustic Live") => "https://www.tekstowo.pl/piosenka,a_ha,take_on_me.html"
      case (_, "Jolka Jolka Pamiętasz")         => "https://www.tekstowo.pl/piosenka,budka_suflera,jolka__jolka_pamietasz_.html"
      case (_, "Wehikuł Czasu")                 => "https://www.tekstowo.pl/piosenka,dzem,wehikul_czasu_.html"
      case (_, "Im Still Standing")             => "https://www.tekstowo.pl/piosenka,elton_john,i_m_still_standing.html"
      case (_, "Kilku Kumpli Weź")              => "https://www.tekstowo.pl/piosenka,anna_jurksztowicz,kilku_kumpli_we_.html"
      case (_, "Dont Give Up On Me")            => "https://www.tekstowo.pl/piosenka,andy_grammer,don_t_give_up_on_me.html"
      case (_, "Im Yours")                      => "https://www.tekstowo.pl/piosenka,jason_mraz,i_m_yours.html"

      case (_, _)                               => s"https://www.tekstowo.pl/piosenka,${tekstowoTitle(author)},${tekstowoTitle(title)}.html"
    }

  }

  def tekstowoTitle(str: String): String = {
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

    str.map { char =>
      replacements.getOrElse(char, char)
    }.toLowerCase
      .replace(" ", "_")
      .replace("-", "_")
  }

}
