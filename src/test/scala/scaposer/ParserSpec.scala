package scaposer

import org.specs2.mutable._

class ParserSpec extends Specification {

  val strPoSimple =
    """
      |msgid "Hello"
      |msgstr "Bonjour"
    """.stripMargin

  "PO singular string" should {
    "be some" in {
      Parser.parsePo(strPoSimple) must beSome
    }
  }


  val strPoWithSlash =
    """
      |msgid "Hello"
      |msgstr "Bon\"jour"
    """.stripMargin

  "PO singular string with \"" should {
    "not be none" in {
      Parser.parsePo(strPoWithSlash) must not beNone
    }
  }

  val strPoWithError =
    """
      |msgid "Hello"
      |msgstr "Bon\"jour
    """.stripMargin

  "PO singular string with \" with no doublequotes at the end" should {
    "be none" in {
      Parser.parsePo(strPoWithError) must beNone
    }
  }
  
  val strPoWithWhiteSpaces =
    """
      |msgid "Hello"
      |msgstr "Bonjour le monde"
      |
      |msgid "Bye"
      |msgstr "tabulation	"
    """.stripMargin

  "PO string with tabulation character" should {
    "not be none" in {
      Parser.parsePo(strPoWithWhiteSpaces) must not beNone
    }
  }

}
