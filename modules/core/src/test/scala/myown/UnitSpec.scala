package myown

import org.scalatest._
import doobie.scalatest._, doobie._, doobie.implicits._
import cats.effect.IO

abstract class UnitSpec extends FreeSpec with Matchers with
  OptionValues with Inside with Inspectors with BeforeAndAfterEach with BeforeAndAfterAll

abstract class UnitDBSpec extends UnitSpec with IOChecker {

  val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:postgres",
    "postgres", ""
  )

  def rawSQL(sql: String): Unit = {
    Fragment.const(sql).update.run.transact(transactor).unsafeRunSync()
  }
}