package services

import models.Product

import scala.math.BigDecimal
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/*
 * To speed-up the development we use a list of products that is hard-coded,
 * In the real life we would probably access a database of products
 */
object Products {
	val dbProductList = List(Product("apple", 0.60), Product("orange", 0.25))

	def calculateProductsTotalPrice(products: List[String]): Future[BigDecimal] = {
		val prices = products.groupBy(p => p).map { case (productName, productList) =>
			getProductPrice(productName).map {
				case Some(price) 	=> ProductOffers.getOfferByProduct(productName).calculateTotal(productList.size, price)
				case _ 						=> BigDecimal(0)
			}
		}

		/* http://doc.akka.io/docs/akka/current/scala/futures.html */
		/* Traverse the list of future prices and sum them up */
		Future.fold(prices)(BigDecimal(0))(_ + _)
	}

	/*
	 * Get a Product given a product name
	 */
	def getProduct(productName: String): Future[Option[Product]] = Future {
		dbProductList.find(_.name == productName)
	}

	/*
	 * Get a product price given a product name
	 */
	def getProductPrice(productName: String): Future[Option[BigDecimal]] = Future {
		dbProductList.find(_.name == productName).map(_.price)
	}
}
