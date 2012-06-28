package scaposer

import org.specs2.mutable._

class ParserSpec extends Specification {

  val strPo1 =
    """
      |msgid "Hello"
      |msgstr "Bonjour"
    """.stripMargin

  "PO singular string" should {
    "be some" in {
      Parser.parsePo(strPo1) must beSome
    }
  }


  val strPo2 =
    """
      |msgid "Hello"
      |msgstr "Bon\"jour"
    """.stripMargin

  "PO singular string with \"" should {
    "not be none" in {
      Parser.parsePo(strPo2) must not beNone
    }
  }

  val strPo3 =
    """
      |msgid "Hello"
      |msgstr "Bon\"jour
    """.stripMargin

  "PO singular string with \" with no doublequotes at the end" should {
    "be none" in {
      Parser.parsePo(strPo3) must beNone
    }
  }
}