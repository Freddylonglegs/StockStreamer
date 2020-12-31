import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Main {

  // needed to run the route
  implicit val system = ActorSystem(Behaviors.empty, "SprayExample")
  // needed for the future map/flatmap in the end and future in fetchItem and saveOrder
  implicit val executionContext = system.executionContext
  // domain model
  final case class Stock(
    lastUpdated: String,
    symbol: String,
    targetHigh: Long,
    targetLow: Long,
    targetMean: Long,
    targetMedian: Long
  )

  def main(args: Array[String]): Unit = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(
      uri = "https://finnhub.io/api/v1/stock/price-target?symbol=NFLX&token=bvlfk0748v6qdeqd82r0"))

    responseFuture
      .onComplete {
        case Success(res) => Unmarshal(res).to[String]
        case Failure(_)   => sys.error("something wrong")
      }
  }
}
