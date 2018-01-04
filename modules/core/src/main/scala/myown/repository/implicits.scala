package myown
package repository

import doobie._, doobie.postgres.implicits._, doobie.postgres._
import cats._, cats.implicits._
import io.circe.{Decoder, Encoder, HCursor, Json}
import io.circe.syntax._
import io.circe.parser._

import scala.reflect.runtime.universe.TypeTag
import models._
import org.postgresql.util.PGobject

object implicits {
  implicit val CurrencyMeta: Meta[Currency] =
    pgEnumString[Currency]("CURRENCY_CODE", Currency.withName, _.entryName)
  implicit val TransactionTypeMeta: Meta[TransactionType] =
    pgEnumString[TransactionType]("TRANSACTION_TYPE", TransactionType.withName, _.entryName)
  implicit val LanguageCodeMeta: Meta[LanguageCode] =
    Meta[String].xmap(s => LanguageCode.withNameOption(s).getOrElse(LanguageCode.custom(s)), _.code)

  implicit val JsonMeta: Meta[Json] =
    Meta.other[PGobject]("json").xmap[Json](
      a => parse(a.getValue).leftMap[Json](e => throw e).merge, // failure raises an exception
      a => {
        val o = new PGobject
        o.setType("json")
        o.setValue(a.noSpaces)
        o
      }
    )

  def codecMeta[A : Encoder : Decoder : TypeTag]: Meta[A] =
    Meta[Json].xmap[A](
      _.as[A].fold[A](throw _, identity),
      _.asJson
    )

  implicit val AccountBalanceItemDecoder: Decoder[(Currency, Money)] = (h: HCursor) =>
    for {
      c <- h.downField("currency_code").as[Currency]
      m <- h.downField("balance").as[BigDecimal]
    } yield c -> Money.unsafe(m, c)

  implicit val AccountBalanceItemEncoder: Encoder[(Currency, Money)] = (m: (Currency, Money)) =>
    Json.obj(
      "currency_code" -> m._1.asJson,
      "balance" -> m._2.raw.asJson
    )

  implicit val AccountBalanceDecoder: Decoder[Map[Currency, Money]] = (h: HCursor) =>
    h.as[List[(Currency, Money)]].map(_.groupBy(_._1).map { case (curr, l) =>
      val volume = l.map(_._2.value).fold(BigDecimal(0))(_ + _)
      curr -> Money(volume, curr)
    })

  implicit val AccountBalanceEncoder: Encoder[Map[Currency, Money]] = (h: Map[Currency, Money]) =>
    h.toList.asJson

  implicit val AccountBalanceMeta: Meta[Map[Currency, Money]] = codecMeta[Map[Currency, Money]]

  implicit val TransactionComposite: Composite[Transaction] =
    Composite[(Long, TransactionType, Option[Long], Option[Long], Currency, BigDecimal, TS)].imap[Transaction] {
      case (id, TransactionType.Deposit, None, Some(to), currency, moneyRaw, createAt) => Transaction.Deposit(id, to, Money.unsafe(moneyRaw, currency), createAt)
      case (id, TransactionType.Adjustment, None, Some(to), currency, moneyRaw, createAt) => Transaction.Adjustment(id, to, Money.unsafe(moneyRaw, currency), createAt)
      case (id, TransactionType.Payment, Some(from), None, currency, moneyRaw, createAt) => Transaction.Payment(id, from, Money.unsafe(moneyRaw, currency), createAt)
      case (id, TransactionType.Transfer, Some(from), Some(to), currency, moneyRaw, createAt) => Transaction.Transfer(id, from, to, Money.unsafe(moneyRaw, currency), createAt)
      case r => sys.error(s"Unknown Transaction type for save $r")
    } {
      case Transaction.Deposit(id, to, money, createAt) => (id, TransactionType.Deposit, None, Some(to), money.currency, money.raw, createAt)
      case Transaction.Adjustment(id, to, money, createAt) => (id, TransactionType.Adjustment, None, Some(to), money.currency, money.raw, createAt)
      case Transaction.Payment(id, from, money, createAt) => (id, TransactionType.Deposit, Some(from), None, money.currency, money.raw, createAt)
      case Transaction.Transfer(id, from, to, money, createAt) => (id, TransactionType.Deposit, Some(from), Some(to), money.currency, money.raw, createAt)
      case r => sys.error(s"Unknown Transaction type for read $r")
    }
}
