package scaposer

import org.specs2.mutable._

class PoSpec extends Specification {
  "Plural-Forms where n != 1" should {
    val po = Parser.parsePo(
      """
        |msgid ""
        |msgstr "Plural-Forms: nplurals=2; plural=n != 1;"
        |
        |msgid "One duckling"
        |msgid_plural "$n ducklings"
        |msgstr[0] "Yksi ankanpoikanen"
        |msgstr[1] "$n ankanpoikasta"
      """.stripMargin
    ).right.get

    "work" in {
      po.tn("One duckling", "$n ducklings", 2) must equalTo ("$n ankanpoikasta")
      po.tn("One duckling", "$n ducklings", 1) must equalTo ("Yksi ankanpoikanen")
      po.tn("One duckling", "$n ducklings", 0) must equalTo ("$n ankanpoikasta")
    }
  }

  "Missing translations" should {
    val po = Parser.parsePo("").right.get

    "be pluralized with the n != 1 rule" in {
      po.tn("One monkey", "$n monkeys", 2) must equalTo ("$n monkeys")
      po.tn("One monkey", "$n monkeys", 1) must equalTo ("One monkey")
      po.tn("One monkey", "$n monkeys", 0) must equalTo ("$n monkeys")
    }
  }

  "Empty translations" should {
    val po = Parser.parsePo(
      """
        |msgid "Could not login."
        |msgstr ""
      """.stripMargin
    ).right.get

    "use msgid instead of the meaningless empty msgstr" in {
      po.t("Could not login.") must equalTo ("Could not login.")
    }
  }
}
