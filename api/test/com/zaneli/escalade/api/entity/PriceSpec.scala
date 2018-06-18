package com.zaneli.escalade.api.entity

import org.specs2.mutable.Specification

class PriceSpec extends Specification {

  "Price" should {
    "toString" in {
      Price(10000).toString must_== "10000.00"
      Price(10000.145).toString must_== "10000.15"
    }
    "multiply" in {
      Price(10000) * Rate(95) must_== Price(9500.00)
      Price(11111.11) * Rate(10) must_== Price(1111.11)
    }
  }
}
