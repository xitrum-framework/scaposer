package com.github.fbaierl.scaposer

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

  "PluralIndexExpressionParser" should {
    "choose valid form for English" in {
      val pluralStr = "nplurals=2; plural=(n != 1);"
      val f = getPluralIndexEvaluator(pluralStr)
      case object Apple
      case object Apples
      val msgStrs = List(Apple, Apples)
      msgStrs(f(1)) must be equalTo (Apple)
      msgStrs(f(2)) must be equalTo (Apples)
      msgStrs(f(5)) must be equalTo (Apples)
      msgStrs(f(11)) must be equalTo (Apples)
      msgStrs(f(21)) must be equalTo (Apples)
    }

    "choose valid form for Russian" in {
      val pluralStr = "nplurals=3; plural=(n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2);"
      val f = getPluralIndexEvaluator(pluralStr)
      case object Yabloko
      case object Yabloka
      case object Yablok
      val msgStrs = List(Yabloko, Yabloka, Yablok)
      msgStrs(f(1)) must be equalTo (Yabloko)
      msgStrs(f(2)) must be equalTo (Yabloka)
      msgStrs(f(5)) must be equalTo (Yablok)
      msgStrs(f(11)) must be equalTo (Yablok)
      msgStrs(f(21)) must be equalTo (Yabloko)
    }

    "choose valid form for Romanian" in {
      val pluralStr = "nplurals=3; plural=n==1 ? 0 : (n==0 || (n%100 > 0 && n%100 < 20)) ? 1 : 2;"
      val f = getPluralIndexEvaluator(pluralStr)
      f(1) must be equalTo (0)
      f(2) must be equalTo (1)
      f(5) must be equalTo (1)
      f(11) must be equalTo (1)
      f(21) must be equalTo (2)
    }
  }

  private def getPluralIndexEvaluator(expr: String) = {
    PluralIndexExpressionParser(expr.replace(" ", "")).map { f =>
      n: Long => f(n).toInt
    }.get
  }
}
