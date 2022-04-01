import cats.effect.Async

import scala.scalajs.js

package object gha {
  implicit class LiftPromise[A](p: js.Promise[A]) {
    def liftTo[F[_]: Async]: F[A] = Async[F].async(cb =>
      Async[F].delay {
        p.`then`[Unit](
          { (v: A) =>
            cb(Right(v))
          },
          js.defined { (e: scala.Any) =>
            cb(Left((e match {
              case th: Throwable => th
              case _             => js.JavaScriptException(e)
            })))
          }
        )
        Some(Async[F].unit)
      }
    )
  }
}
