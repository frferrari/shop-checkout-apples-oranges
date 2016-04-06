package services

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/*
 * To speed-up development we use an hard-coded structure that maps a product to an offer
 * In the real life we would probably access a database of productOffers
 */ 
object ProductOffers {
	val offersByProduct: Map[String, Offer] = Map("apple" -> new BuyOneGetOneFree, "orange" -> new ThreeForThePriceOfTwo)
	val noOffer = new NoOffer

	def getOfferByProduct(productName: String): Offer = offersByProduct.getOrElse(productName, noOffer)
}