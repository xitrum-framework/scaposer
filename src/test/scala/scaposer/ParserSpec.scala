package scaposer

import org.specs2.mutable._

class ParserSpec extends Specification {
  val strPoSimple =
    """
      |msgid "Hello"
      |msgstr "Bonjour"
    """.stripMargin

  "PO singular string" should {
    "be Right" in {
      Parser.parsePo(strPoSimple) must beRight
    }
  }

  //----------------------------------------------------------------------------

  val strPoWithSlash =
    """
      |msgid "Hello"
      |msgstr "Bon\"jour"
    """.stripMargin

  "PO singular string with \"" should {
    "be Right" in {
      Parser.parsePo(strPoWithSlash) must beRight
    }
  }

  //----------------------------------------------------------------------------

  val strPoWithError =
    """
      |msgid "Hello"
      |msgstr "Bon\"jour
    """.stripMargin

  "PO singular string with \" with no doublequotes at the end" should {
    "be Left" in {
      Parser.parsePo(strPoWithError) must beLeft
    }
  }

  //----------------------------------------------------------------------------

  val strPoWithWhiteSpaces =
    """
      |msgid "Hello"
      |msgstr "Bonjour le monde"
      |
      |msgid "Bye"
      |msgstr "tabulation	"
    """.stripMargin

  "PO string with tabulation character" should {
    "be Right" in {
      Parser.parsePo(strPoWithWhiteSpaces) must beRight
    }
  }
}
