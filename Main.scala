import cats.*
import cats.syntax.all.*

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
  import com.comcast.ip4s.{host, port, Port}

  val port = port"8080"

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
        .withPort(port)
        .withHttpApp(
          app
        )
        .build

  val serverResource = httpUtils[IO]().server
  val clientResourse =  EmberClientBuilder.default[IO].build

  def run = for {
      httpService <- Deferred[IO, Unit]
      _ <- serverResource.useForeverAsService(httpService)
      _ <- clientResourse.useWithService(httpService)(printAndWait)
  } yield ()



  def printAndWait(client: Client[IO]): IO[Unit] = 
    def loop: IO[Unit] = for {
         _ <- client.expect[String](s"http://localhost:${port}/hello/YP") >>= IO.println
         _ <- IO.sleep(1.second)
         _ <- loop
    } yield ()
    loop


  extension[A](r: Resource[IO,A])
    def useForeverAsService(d: Deferred[IO, Unit]): IO[FiberIO[Nothing]] = for {
        _ <- d.complete(())
        fiber <- r.useForever.start
    } yield fiber

    def useWithService[B](d: Deferred[IO, Unit])(uf: A => IO[B]): IO[B] = for {
      _ <- d.get
      res <- r.use(uf)
    } yield res
