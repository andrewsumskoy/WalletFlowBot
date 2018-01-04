package myown.models

import org.scalatest._

class MoneySpec extends FreeSpec with Matchers{
  "A Money" - {
    "should wrap un wrap" in {
      val m = Money.unsafe(123456.78, Currency.USD)
      m.value shouldBe BigDecimal(1234.56)
      m.raw shouldBe BigDecimal(123456)
    }
  }
}
