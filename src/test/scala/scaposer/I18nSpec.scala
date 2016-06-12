package scaposer

import org.specs2.mutable._

class I18nSpec extends Specification {
  "Plural-Forms where n != 1" should {
    val po =
      """
       |msgid ""
       |msgstr "Plural-Forms: nplurals=2; plural=n != 1;"
       |
       |msgid "One duckling"
       |msgid_plural "$n ducklings"
       |msgstr[0] "Yksi ankanpoikanen"
       |msgstr[1] "$n ankanpoikasta"
     """.stripMargin
    val translations = Parser.parse(po).right.get
    val i18n = I18n(translations)

    "work" in {
      i18n.tn("One duckling", "$n ducklings", 2) must equalTo ("$n ankanpoikasta")
      i18n.tn("One duckling", "$n ducklings", 1) must equalTo ("Yksi ankanpoikanen")
      i18n.tn("One duckling", "$n ducklings", 0) must equalTo ("$n ankanpoikasta")
    }
  }

  "Missing translations" should {
    val translations = Parser.parse("").right.get
    val i18n = I18n(translations)

    "use msgid" in {
      i18n.t("Could not login.") must equalTo ("Could not login.")
    }

    "be pluralized with the n != 1 rule" in {
      i18n.tn("One monkey", "$n monkeys", 2) must equalTo ("$n monkeys")
      i18n.tn("One monkey", "$n monkeys", 1) must equalTo ("One monkey")
      i18n.tn("One monkey", "$n monkeys", 0) must equalTo ("$n monkeys")
    }
  }

  "Empty translations" should {
    val po =
      """
        |msgid "Could not login."
        |msgstr ""
      """.stripMargin
    val translations = Parser.parse(po).right.get
    val i18n = I18n(translations)

    "use msgid instead of the meaningless empty msgstr" in {
      i18n.t("Could not login.") must equalTo ("Could not login.")
    }
  }
}
