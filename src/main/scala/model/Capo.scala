package model

import model.Capo.NoCapo
import model.Capo.`10thFret`
import model.Capo.`11thFret`
import model.Capo.`1stFret`
import model.Capo.`2ndFret`
import model.Capo.`3rdFret`
import model.Capo.`4thFret`
import model.Capo.`5thFret`
import model.Capo.`6thFret`
import model.Capo.`7thFret`
import model.Capo.`8thFret`
import model.Capo.`9thFret`

sealed trait Capo {
  override def toString: String = {
    this match {
      case NoCapo     => "no capo"
      case `1stFret`  => "1st fret"
      case `2ndFret`  => "2nd fret"
      case `3rdFret`  => "3rd fret"
      case `4thFret`  => "4th fret"
      case `5thFret`  => "5th fret"
      case `6thFret`  => "6th fret"
      case `7thFret`  => "7th fret"
      case `8thFret`  => "8th fret"
      case `9thFret`  => "9th fret"
      case `10thFret` => "10th fret"
      case `11thFret` => "11th fret"
    }
  }

}
object Capo {

  def fromString(str: String): Option[Capo] = {
    str match {
      case "no capo"   => Some(NoCapo)
      case "1st fret"  => Some(`1stFret`)
      case "2nd fret"  => Some(`2ndFret`)
      case "3rd fret"  => Some(`3rdFret`)
      case "4th fret"  => Some(`4thFret`)
      case "5th fret"  => Some(`5thFret`)
      case "6th fret"  => Some(`6thFret`)
      case "7th fret"  => Some(`7thFret`)
      case "8th fret"  => Some(`8thFret`)
      case "9th fret"  => Some(`9thFret`)
      case "10th fret" => Some(`10thFret`)
      case "11th fret" => Some(`11thFret`)

      case a @ _ =>
        if (a.trim.nonEmpty) scribe.warn(s"Not recognized capo option: $a")
        None
    }
  }

  case object NoCapo extends Capo
  case object `1stFret` extends Capo
  case object `2ndFret` extends Capo
  case object `3rdFret` extends Capo
  case object `4thFret` extends Capo
  case object `5thFret` extends Capo
  case object `6thFret` extends Capo
  case object `7thFret` extends Capo
  case object `8thFret` extends Capo
  case object `9thFret` extends Capo
  case object `10thFret` extends Capo
  case object `11thFret` extends Capo

}
