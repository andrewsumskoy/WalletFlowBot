package myown
package telegram

import models._

class ViewsSpec extends UnitSpec {
  "View" - {
    "start" - {
      "empty account list" in {
        val correct =
          """
            |Welcome USER! I`m your personal finance assistant.
            |
            |Create you first account with /newaccount command or simple type like
            |`new account 💵Cash`
          """.stripMargin.trim
        val ret = views.start("USER", Seq.empty)
        ret shouldBe correct
      }

      "account contains one item and one balance" in {
        val acc = new AccountView {
          val title = "acc"
          val balance = Map(Currency.USD -> Money(100, Currency.USD))
        }
        val correct =
          """
            |Welcome USER! I`m your personal finance assistant.
            |
            |acc $100
          """.stripMargin.trim
        val ret = views.start("USER", Seq(acc))
        ret shouldBe correct
      }

      "account contains one item and two balance" in {
        val acc = new AccountView {
          val title = "acc"
          val balance = Map(
            Currency.USD -> Money(100, Currency.USD),
            Currency.EUR -> Money(50.11, Currency.EUR)
          )
        }
        val correct =
          """
            |Welcome USER! I`m your personal finance assistant.
            |
            |acc $100, €50.11
          """.stripMargin.trim
        val ret = views.start("USER", Seq(acc))
        ret shouldBe correct
      }

      "account contains two item and two balance" in {
        val acc = new AccountView {
          val title = "acc"
          val balance = Map(
            Currency.USD -> Money(100, Currency.USD),
            Currency.EUR -> Money(50.11, Currency.EUR)
          )
        }
        val correct =
          """
            |Welcome USER! I`m your personal finance assistant.
            |
            |acc $100, €50.11
            |acc $100, €50.11
          """.stripMargin.trim
        val ret = views.start("USER", Seq(acc, acc))
        ret shouldBe correct
      }
    }
  }
}
