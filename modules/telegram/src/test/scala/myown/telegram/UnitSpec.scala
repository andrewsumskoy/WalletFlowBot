package myown.telegram

import org.scalatest._

abstract class UnitSpec extends FreeSpec with Matchers with
  OptionValues with Inside with Inspectors with BeforeAndAfterEach with BeforeAndAfterAll
