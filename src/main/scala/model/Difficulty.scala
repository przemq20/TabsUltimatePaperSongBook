package model

import model.Difficulty.AbsoluteBeginner
import model.Difficulty.Advanced
import model.Difficulty.Beginner
import model.Difficulty.Intermediate

sealed trait Difficulty {
  override def toString: String = {
    this match {
      case Intermediate     => "intermediate"
      case Beginner         => "beginner"
      case AbsoluteBeginner => "absolute beginner"
      case Advanced         => "advanced"
    }
  }
}
object Difficulty {
  def fromString(str: String): Option[Difficulty] = {
    str match {
      case "intermediate"      => Some(Intermediate)
      case "beginner"          => Some(Beginner)
      case "absolute beginner" => Some(AbsoluteBeginner)
      case "advanced"          => Some(Advanced)
      case a @ _               =>
        if (a.trim.isEmpty) scribe.warn(s"Not recognized difficulty option: $a")
        None
    }
  }

  case object Intermediate extends Difficulty
  case object Beginner extends Difficulty
  case object AbsoluteBeginner extends Difficulty
  case object Advanced extends Difficulty

}
