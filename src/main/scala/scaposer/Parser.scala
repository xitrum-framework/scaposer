package scaposer

import java.io.Reader
import scala.util.parsing.combinator.JavaTokenParsers

/**  Parses .po file to [[Translation]]s. */
object Parser {
  private val parser = new Parser

  //----------------------------------------------------------------------------
  // We only want to expose parsePo methods, instead of exposing all methods
  // inherited from JavaTokenParsers, in class Parser below.

  def parse(po: CharSequence) = parser.parsePo(po)
  def parse(po: Reader) = parser.parsePo(po)
}

/** See http://www.gnu.org/software/hello/manual/gettext/PO-Files.html */
private class Parser extends JavaTokenParsers {
  /**
   * Removes the first and last quote (") character of strings and concat them.
   */
  private def mergeStrs(quoteds: List[String]): String = {
    val unquoted = quoteds.foldLeft("") { (acc, quoted) =>
      acc + quoted.substring(1, quoted.length - 1)
    }

    // Unescape
    unquoted.
      replace("""\n""", "\n").
      replace("""\r""", "\r").
      replace("""\t""", "\t").
      replace("""\\""", "\\").
      replace("""\"""", "\"")
  }

  /**
   * Double quotes (`"`) enclosing a sequence of:
   *
   * - Any character except double quotes, control characters or backslash (`\`)
   * - A backslash followed by a slash, another backslash, or one of the letters
   *    `b`, `f`, `n`, `r` or `t`.
   * - `\` followed by `u` followed by four hexadecimal digits
   */
  private val reStringLiteral: Parser[String] =
    ("\""+"""((\\\")|\p{Space}|\\u[a-fA-F0-9]{4}|[^"\p{Cntrl}\\]|\\[\\/bfnrt])*"""+"\"").r

  // Scala regex is single line by default
  private def comment = rep(regex("^#.*".r))

  private def msgctxt = "msgctxt" ~ rep(reStringLiteral) ^^ {
    case _ ~ quoteds => mergeStrs(quoteds)
  }

  private def msgid = "msgid" ~ rep(reStringLiteral) ^^ {
    case _ ~ quoteds => mergeStrs(quoteds)
  }

  private def msgidPlural = "msgid_plural" ~ rep(reStringLiteral) ^^ {
    case _ ~ quoteds => mergeStrs(quoteds)
  }

  private def msgstr = "msgstr" ~ rep(reStringLiteral) ^^ {
    case _ ~ quoteds => mergeStrs(quoteds)
  }

  private def msgstrN = "msgstr[" ~ wholeNumber ~ "]" ~ rep(reStringLiteral) ^^ {
    case _ ~ number ~ _ ~ quoteds => (number.toInt, mergeStrs(quoteds))
  }

  private def singular =
    (opt(comment) ~ opt(msgctxt) ~
     opt(comment) ~ msgid ~
     opt(comment) ~ msgstr ~ opt(comment)) ^^ {
    case ctxCommentso ~ ctxo ~ singularCommentso ~ singular ~ strCommentso ~ str ~ otherCommentso =>
      SingularTranslation(
        ctxCommentso.getOrElse(Seq.empty),
        ctxo.getOrElse(""),
        singularCommentso.getOrElse(Seq.empty),
        singular,
        strCommentso.getOrElse(Seq.empty),
        str,
        otherCommentso.getOrElse(Seq.empty)
      )
  }

  private def plural =
    (opt(comment) ~ opt(msgctxt) ~
     opt(comment) ~ msgid ~
     opt(comment) ~ msgidPlural ~
     opt(comment) ~ rep(msgstrN) ~ opt(comment)) ^^ {
    case ctxCommentso ~ ctxo ~ singularCommentso ~ singular ~ pluralCommentso ~ plural ~ strsCommentso ~ n_strs ~ otherCommentso =>
      val strs = n_strs.sorted.map { case (n, str) => str }
      PluralTranslation(
        ctxCommentso.getOrElse(Seq.empty),
        ctxo.getOrElse(""),
        singularCommentso.getOrElse(Seq.empty),
        singular,
        pluralCommentso.getOrElse(Seq.empty),
        plural,
        strsCommentso.getOrElse(Seq.empty),
        strs,
        otherCommentso.getOrElse(Seq.empty)
      )
  }

  private def exp = rep(singular | plural)

  //----------------------------------------------------------------------------

  def parsePo(po: CharSequence): Either[ParseFailure, Seq[Translation]] = {
    val parseResult = parseAll(exp, po)
    translationsFromParseResult(parseResult)
  }

  def parsePo(po: Reader): Either[ParseFailure, Seq[Translation]] = {
    val parseResult = parseAll(exp, po)
    translationsFromParseResult(parseResult)
  }

  private def translationsFromParseResult(
    parseResult: ParseResult[List[Translation]]
  ): Either[ParseFailure, Seq[Translation]] = parseResult match {
    case NoSuccess(msg, next) =>
      val errorPos = next.pos
      Left(new ParseFailure(msg, errorPos))

    case Success(translations, _) =>
      Right(translations)
  }
}
