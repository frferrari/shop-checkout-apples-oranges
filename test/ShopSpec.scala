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

		"return 0 for an empty list of products" in {
			val request = FakeRequest("GET", "/checkout").withJsonBody(Json.parse(
				s"""[ ]""".stripMargin))
			val postResult = call(common.shopController.checkout, request)
			val jsonResult = contentAsJson(postResult)

			status(postResult) mustEqual OK
			(jsonResult \ "total").as[BigDecimal] should equal(BigDecimal(0.0))
		}

		"return 0 for a list of invalid product names" in {
			val request = FakeRequest("GET", "/checkout").withJsonBody(Json.parse(
				s"""[ "apples", "oranges" ]""".stripMargin))
			val postResult = call(common.shopController.checkout, request)
			val jsonResult = contentAsJson(postResult)

			status(postResult) mustEqual OK
			(jsonResult \ "total").as[BigDecimal] should equal(BigDecimal(0.0))
		}

		"calculate the total price for 1 apple and 1 orange" in {
			val request = FakeRequest("GET", "/checkout").withJsonBody(Json.parse(
				s"""[ "apple", "orange" ]""".stripMargin))
			val postResult = call(common.shopController.checkout, request)
			val jsonResult = contentAsJson(postResult)

			status(postResult) mustEqual OK
			(jsonResult \ "total").as[BigDecimal] should equal(BigDecimal(0.85))
		}

		"calculate the total price for 4 apples (pay for 2 apples) and 1 orange" in {
			val request = FakeRequest("GET", "/checkout").withJsonBody(Json.parse(
				s"""[ "apple", "apple", "orange", "apple", "apple" ]""".stripMargin))
			val postResult = call(common.shopController.checkout, request)
			val jsonResult = contentAsJson(postResult)

			status(postResult) mustEqual OK
			(jsonResult \ "total").as[BigDecimal] should equal(BigDecimal(1.45))
		}

		"calculate the total price for 3 apples (pay for 2 apples) and 5 oranges (pay for 4 oranges) and discard an unknown product" in {
			val request = FakeRequest("GET", "/checkout").withJsonBody(Json.parse(
				s"""[ "orange", "kiwi", "apple", "orange", "apple", "orange", "orange", "apple", "orange" ]""".stripMargin))
			val postResult = call(common.shopController.checkout, request)
			val jsonResult = contentAsJson(postResult)

			status(postResult) mustEqual OK
			(jsonResult \ "total").as[BigDecimal] should equal(BigDecimal(2.20))
		}

		"calculate the total price and discard invalid product names" in {
			val request = FakeRequest("GET", "/checkout").withJsonBody(Json.parse(
				s"""[ "apples", "apple", "oranges" ]""".stripMargin))
			val postResult = call(common.shopController.checkout, request)
			val jsonResult = contentAsJson(postResult)

			status(postResult) mustEqual OK
			(jsonResult \ "total").as[BigDecimal] should equal(BigDecimal(0.60))
		}
	}
}
