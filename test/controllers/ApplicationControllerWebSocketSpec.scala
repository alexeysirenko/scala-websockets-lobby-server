package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import com.github.andyglow.websocket._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.JsValue

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class ApplicationControllerWebSocketSpec extends PlaySpec {

  "Application" should {

    "Test websocket" in {
      lazy val port: Int = 31337
      val app = new GuiceApplicationBuilder().build()
      Helpers.running(TestServer(port, app)) {

        val lobbyEndpointUrl = s"ws://localhost:$port/lobby"
        println(lobbyEndpointUrl)
        val cli = WebsocketClient[String](lobbyEndpointUrl) { case str =>
          println(s"<<| $str")
        }

        // 4. open websocket
        val ws = cli.open()

        // 5. send messages
        ws ! "{\n \"$type\": \"ping\",\n \"seq\": 1\n}"
      }
    }

  }

}
