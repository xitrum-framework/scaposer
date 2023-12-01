package scaposer

import org.specs2.mutable._

class I18nSpec extends Specification {
  "Plural-Forms where n != 1" should {
    val po =
      """
       |msgid ""
       |msgstr ""
       |"Content-Type: text/plain; charset=UTF-8\n"
       |"Content-Transfer-Encoding: 8bit\n"
       |"Plural-Forms: nplurals=2; plural=n != 1;\n"
       |"X-Generator: Pootle 2.5.1\n"
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
  
  "Plural-Forms: nplurals=2; plural=(n != 1);" should {
    val po =
      """
       |msgid ""
       |msgstr ""
       |"Content-Type: text/plain; charset=UTF-8\n"
       |"Content-Transfer-Encoding: 8bit\n"
       |"Plural-Forms: nplurals=2; plural=(n != 1);\n"
       |"X-Generator: Pootle 2.5.1\n"
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
  
  "Plural-Forms: nplurals=3; plural=(n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2);" should {
    val po =
      """
       |msgid ""
       |msgstr ""
       |"Content-Type: text/plain; charset=UTF-8\n"
       |"Content-Transfer-Encoding: 8bit\n"
       |"Plural-Forms: nplurals=3; plural=(n%10==1 && n%100!=11 ? 0 : n%10>=2 && "
       |"n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2);\n"
       |"X-Generator: Pootle 2.5.1\n"
       |
       |msgid "$n яблок"
       |msgid_plural "$n яблок"
       |msgstr[0] "Одно яблоко"
       |msgstr[1] "$n яблока"
       |msgstr[2] "$n яблок"
     """.stripMargin
    val translations = Parser.parse(po).right.get
    val i18n = I18n(translations)

    "work" in {
      i18n.tn("$n яблок", "$n яблок", 2) must equalTo ("$n яблока")
      i18n.tn("$n яблок", "$n яблок", 1) must equalTo ("Одно яблоко")
      i18n.tn("$n яблок", "$n яблок", 0) must equalTo ("$n яблок")
      i18n.tn("$n яблок", "$n яблок", 10) must equalTo ("$n яблок")
    }
  }
}
