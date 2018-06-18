package com.zaneli.escalade.api.entity

import org.specs2.mutable.Specification

class RateSpec extends Specification {

  "Rate" should {
    "toString" in {
      Rate(10).toString must_== "10.00"
      Rate(12.345).toString must_== "12.35"
    }
    "reciprocal" in {
      Rate(10).reciprocal must_== Rate(90)
      Rate(0.01).reciprocal must_== Rate(99.99)
    }
    "multiply" in {
      Price(100) * Rate(5) must_== Price(5)
      Price(100) * Rate(0.1).reciprocal must_== Price(99.9)
    }
  }
}
