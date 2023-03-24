import cats.*
import cats.effect.*
import cats.implicits.*
import cats.effect.implicits.*
import org.http4s.server.Server

import CanEqualInstances.given

object MyClass extends IOApp.Simple:
  import org.http4s
  import com.comcast.ip4s.{host, port}

  class httpUtils[F[_]] extends http4s.dsl.Http4sDsl[F]:
    def app(using Monad[F]) =
      http4s.HttpRoutes
        .of[F] {
          case GET -> Root / "hello" / name =>
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
  def run = httpUtils[IO]().server.useForever
