import org.scalatest._
import org.scalatestplus.play._
import org.scalactic.Explicitly._
import org.scalactic.StringNormalizations._
import Matchers._

import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import akka.stream.Materializer
import scala.math.BigDecimal

import controllers.ShopController

class ShopSpec extends PlaySpec with OneServerPerSuite with HtmlUnitFactory {

	def common = new {
		val shopController = new ShopController
	}

	/*
	 *
	 */
	"ShopController" should {

		implicit lazy val materializer: Materializer = app.materializer

		"calculate the total price for a list of unique products" in {
			val request = FakeRequest("GET", "/checkout").withJsonBody(Json.parse(
				s"""[ "apple", "orange" ]""".stripMargin))
			val postResult = call(common.shopController.checkout, request)
			val jsonResult = contentAsJson(postResult)

			status(postResult) mustEqual OK
			(jsonResult \ "total").as[BigDecimal] should equal(BigDecimal(0.85))
		}

		"calculate the total price for a list of duplicated products" in {
			val request = FakeRequest("GET", "/checkout").withJsonBody(Json.parse(
				s"""[ "apple", "orange", "apple", "apple", "orange" ]""".stripMargin))
			val postResult = call(common.shopController.checkout, request)
			val jsonResult = contentAsJson(postResult)

			status(postResult) mustEqual OK
			(jsonResult \ "total").as[BigDecimal] should equal(BigDecimal(2.3))
		}

		"calculate the total price and dismiss invalid product names" in {
			val request = FakeRequest("GET", "/checkout").withJsonBody(Json.parse(
				s"""[ "apples", "apple", "oranges" ]""".stripMargin))
			val postResult = call(common.shopController.checkout, request)
			val jsonResult = contentAsJson(postResult)

			status(postResult) mustEqual OK
			(jsonResult \ "total").as[BigDecimal] should equal(BigDecimal(0.60))
		}

		"dismiss a full list of invalid product names" in {
			val request = FakeRequest("GET", "/checkout").withJsonBody(Json.parse(
				s"""[ "apples", "oranges" ]""".stripMargin))
			val postResult = call(common.shopController.checkout, request)
			val jsonResult = contentAsJson(postResult)

			status(postResult) mustEqual OK
			(jsonResult \ "total").as[BigDecimal] should equal(BigDecimal(0.0))
		}
	}
}
