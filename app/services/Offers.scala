package services

trait Offer {
  def calculateTotal(productQty: Int, productPrice: BigDecimal): BigDecimal
}

class BuyOneGetOneFree extends Offer {
  def calculateTotal(productQty: Int, productPrice: BigDecimal): BigDecimal = {
    val offerQty = (productQty / 2).toDouble + (productQty.toDouble % 2)

    offerQty * productPrice
  }
}

class ThreeForThePriceOfTwo extends Offer {
  def calculateTotal(productQty: Int, productPrice: BigDecimal): BigDecimal = {
    val p1 = (productQty / 3).toDouble * productPrice * 2.0
    val p2 = (productQty.toDouble % 3) * productPrice

    p1 + p2
  }
}

class NoOffer extends Offer {
  def calculateTotal(productQty: Int, productPrice: BigDecimal): BigDecimal = productQty * productPrice
}