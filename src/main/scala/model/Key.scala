package model

import model.Key.A
import model.Key.Am
import model.Key.B
import model.Key.Bm
import model.Key.C
import model.Key.Cm
import model.Key.D
import model.Key.Dm
import model.Key.E
import model.Key.Em
import model.Key.F
import model.Key.Fm
import model.Key.G
import model.Key.Gm
import model.Key.`A#`
import model.Key.`A#m`
import model.Key.`C#`
import model.Key.`C#m`
import model.Key.`D#`
import model.Key.`D#m`
import model.Key.`F#`
import model.Key.`F#m`
import model.Key.`G#`
import model.Key.`G#m`

sealed trait Key {
  override def toString: String = {
    this match {
      case A     => "A"
      case `A#`  => "A#"
      case B     => "B"
      case C     => "C"
      case `C#`  => "C#"
      case D     => "D"
      case `D#`  => "D#"
      case E     => "E"
      case F     => "F"
      case `F#`  => "`#"
      case G     => "G"
      case `G#`  => "G#"
      case Am    => "Am"
      case `A#m` => "A#m"
      case Bm    => "Bm"
      case Cm    => "Cm"
      case `C#m` => "C#m"
      case Dm    => "Dm"
      case `D#m` => "D#m"
      case Em    => "Em"
      case Fm    => "Fm"
      case `F#m` => "F#m"
      case Gm    => "Gm"
      case `G#m` => "G#m"
    }
  }

}
object Key {

  def fromString(str: String): Option[Key] = {
    str match {
      case "A"           => Some(A)
      case "A#" | "Bb"   => Some(`A#`)
      case "B"           => Some(B)
      case "C"           => Some(C)
      case "C#" | "Db"   => Some(`C#`)
      case "D"           => Some(D)
      case "D#" | "Eb"   => Some(`D#`)
      case "E"           => Some(E)
      case "F"           => Some(`F`)
      case "F#" | "Gb"   => Some(`F#`)
      case "G"           => Some(G)
      case "G#" | "Ab"   => Some(`G#`)
      case "Am"          => Some(Am)
      case "A#m" | "Bbm" => Some(`A#m`)
      case "Bm"          => Some(Bm)
      case "Cm"          => Some(Cm)
      case "C#m" | "Dbm" => Some(`C#m`)
      case "Dm"          => Some(Dm)
      case "D#m" | "Ebm" => Some(`D#m`)
      case "Em"          => Some(Em)
      case "Fm"          => Some(`Fm`)
      case "F#m" | "Gbm" => Some(`F#m`)
      case "Gm"          => Some(Gm)
      case "G#m" | "Abm" => Some(`G#m`)
      case a @ _         =>
        if (a.trim.isEmpty) scribe.warn(s"Not recognized key option: $a")
        None
    }
  }

  case object A extends Key
  case object `A#` extends Key
  case object B extends Key
  case object C extends Key
  case object `C#` extends Key
  case object D extends Key
  case object `D#` extends Key
  case object E extends Key
  case object F extends Key
  case object `F#` extends Key
  case object G extends Key
  case object `G#` extends Key
  case object Am extends Key
  case object `A#m` extends Key
  case object Bm extends Key
  case object Cm extends Key
  case object `C#m` extends Key
  case object Dm extends Key
  case object `D#m` extends Key
  case object Em extends Key
  case object Fm extends Key
  case object `F#m` extends Key
  case object Gm extends Key
  case object `G#m` extends Key
}
