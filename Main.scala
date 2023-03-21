//> using scala "3.2.2"
// Всяких полезностей
//> using options "-Ykind-projector:underscores", "-explain", "-deprecation", "-rewrite", "-indent", "-source", "future-migration"
//> using lib "org.typelevel::cats-effect::3.4.8"

import cats.*
import cats.effect.*
import cats.implicits.*
import cats.effect.implicits.*
object MyClass extends IOApp.Simple:
  def run = IO.println("Hello, world!")
