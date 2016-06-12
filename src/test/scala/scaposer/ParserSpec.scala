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
      val p = Parser.parse(strPoSimple)
      p must beRight

      val translations = p.right.get
      translations mustEqual Seq(SingularTranslation(
        Seq.empty, "", Seq.empty, "Hello", Seq.empty, "Bonjour", Seq.empty
      ))
    }
  }

  //----------------------------------------------------------------------------

  val strPoWithSlash =
    """
      |msgid "Hello \"world\""
      |msgstr "Bonjour \"le monde\""
      |
      |msgid "Slashes \n\r\t\\"
      |msgstr "Slashes \n\r\t\\"
    """.stripMargin

  "PO singular string with \"" should {
    "be Right" in {
      val p = Parser.parse(strPoWithSlash)
      p must beRight

      val translations = p.right.get
      translations mustEqual Seq(
        SingularTranslation(
          Seq.empty, "", Seq.empty, "Hello \"world\"", Seq.empty, "Bonjour \"le monde\"", Seq.empty
        ),
        SingularTranslation(
          Seq.empty, "", Seq.empty, "Slashes \n\r\t\\", Seq.empty, "Slashes \n\r\t\\", Seq.empty
        )
      )
    }
  }

  //----------------------------------------------------------------------------

  val strPoWithError =
    """
      |msgid "Hello"
      |msgstr "Bonjour
    """.stripMargin

  "PO singular string with no doublequotes at the end" should {
    "be Left" in {
      Parser.parse(strPoWithError) must beLeft
    }
  }

  //----------------------------------------------------------------------------

  val strPoWithWhiteSpaces =
    """
      |msgid "Hello world"
      |msgstr "Bonjour le monde"
      |
      |msgid "Bye"
      |msgstr "tabulation	"
    """.stripMargin

  "PO string with tabulation character" should {
    "be Right" in {
      Parser.parse(strPoWithWhiteSpaces) must beRight
    }
  }
}
