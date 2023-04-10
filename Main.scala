import cats.*
import cats.effect.*
import cats.implicits.*
import cats.effect.implicits.*
import org.http4s.server.Server

import org.http4s.ember.client.EmberClientBuilder
import org.http4s.client.Client

import scala.concurrent.duration.*


import CanEqualInstances.given
import org.http4s.ClientTypes

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

  val serverResource = httpUtils[IO]().server
  val clientResourse =  EmberClientBuilder.default[IO].build

  val both = serverResource.both(clientResourse)

  def printAndWait(client: Client[IO]) = (
         (client.expect[String]("http://localhost:8080/hello/Ember") >>= IO.println)
          *> IO.sleep(1.second)
        ).foreverM

  def run = for {
    _ <- IO.println("Started")
    _ <- both.use(x => printAndWait(x._2))
  } yield ()

