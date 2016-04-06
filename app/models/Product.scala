package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Product(name: String, price: BigDecimal)
