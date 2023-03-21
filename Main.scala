//> using scala "3.2.2"
// Всяких полезностей
//> using options "-Ykind-projector:underscores", "-explain", "-deprecation", "-rewrite", "-indent", "-source", "future-migration"
//> using lib "org.typelevel::cats-effect::3.4.8"
// HTTP4S
//> using lib "org.http4s::http4s-ember-server::0.23.18"
//> using lib "org.http4s::http4s-ember-client::0.23.18"
//> using lib "org.http4s::http4s-dsl::0.23.18"

import cats.*
import cats.effect.*
import cats.implicits.*
import cats.effect.implicits.*
import org.http4s.server.Server
object MyClass extends IOApp.Simple:
  import org.http4s
  import com.comcast.ip4s.*
  class httpUtils[F[_]] extends http4s.dsl.Http4sDsl[F]:
    def app(using Monad[F]) =
      http4s.HttpRoutes
        .of[F] { case GET -> Root / "hello" / name =>
          Ok(s"Hello, $name.")
        }
        .orNotFound

    def server(using Async[F]): Resource[F, Server] =
      http4s.ember.server.EmberServerBuilder
        .default[F]
        .withPort(port"8080")
        .withHttpApp(
          app
        )
        .build
  def run = httpUtils[IO]().server.use(_ => IO.never)
