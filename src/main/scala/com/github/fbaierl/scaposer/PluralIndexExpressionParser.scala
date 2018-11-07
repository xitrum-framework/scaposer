package com.github.fbaierl.scaposer

import scala.util.parsing.combinator.RegexParsers

object PluralIndexExpressionParser extends RegexParsers {
  private def wholeExpr = nplurals ~ assign ~ integerConst ~ endExpr ~ plural ~ assign ~ subexpr ~ endExpr ^^ {
      case _ ~ _ ~ maxIndex ~ _ ~ _ ~ _ ~ f ~ _ => x: Long => Math.min(f(Math.max(x, 0L)), maxIndex)
    }

    def apply(input: String): ParseResult[Long => Long] = parseAll(wholeExpr, input)

    final private val assign = "="
    final private val equals = "=="
    final private val notEquals = "!="
    final private val greater = ">"
    final private val less = "<"
    final private val lessOrEquals = "<="
    final private val greaterOrEquals = ">="

    final private val multuply = "*"
    final private val divide = "/"
    final private val mod = "%"

    final private val plus = "+"
    final private val minus = "-"

    final private val logicalAnd = "&&"
    final private val logicalOr = "||"
    final private val negation = "!"

    final private val openBrace = "("
    final private val closeBrace = ")"

    final private val nplurals = "nplurals"
    final private val plural = "plural"
    final private val endExpr = ";"

    private def number = """\d+""".r ^^ { str =>
      x: Long => str.toLong
    }

    private def integerConst = """\d+""".r ^^ (_.toInt)

    private def n = "n" ^^ { _ =>
      x: Long => x.toLong
    }

    private def subexpr = ternary | logicToNumber | expr

    private def value: Parser[Long => Long] = number | n | (openBrace ~> subexpr <~ closeBrace)

    private def term: Parser[Long => Long] = value ~ rep(multuply ~ value | divide ~ value | mod ~ value) ^^ {
      case number ~ list => (number /: list) {
        case (x, `multuply` ~ y) => t: Long => x(t) * y(t)
        case (x, `divide` ~ y) => t: Long => x(t) / y(t)
        case (x, `mod` ~ y) => t: Long => x(t) % y(t)
      }
    }

    private def expr: Parser[Long => Long] = term ~ rep(plus ~ term | minus ~ term) ^^ {
      case number ~ list => (number /: list) {
        case (x, `plus` ~ y) => t: Long => x(t) + y(t)
        case (x, `minus` ~ y) => t: Long => x(t) - y(t)
      }
    }

    private def compare = expr ~ (greaterOrEquals| lessOrEquals | notEquals | equals | greater | less) ~ expr ^^ {
      case a ~ `greaterOrEquals` ~ b => x: Long => a(x) >= b(x)
      case a ~ `lessOrEquals` ~ b => x: Long => a(x) <= b(x)
      case a ~ `notEquals` ~ b => x: Long => a(x) != b(x)
      case a ~ `equals` ~ b => x: Long => a(x) == b(x)
      case a ~ `greater` ~ b => x: Long => a(x) > b(x)
      case a ~ `less` ~ b => x: Long => a(x) < b(x)
    }

    private def logic: Parser[Long => Boolean] = (openBrace ~> (logic) <~ closeBrace) | (compare ~ rep(logicalAnd ~ logic | logicalOr ~ logic) ^^ {
      case compare ~ list => list.foldLeft(compare) {
        case (x, `logicalAnd` ~ y) => t: Long => x(t) && y(t)
        case (x, `logicalOr` ~ y) => t: Long => x(t) || y(t)
      }
    })

    private def logicToNumber: Parser[Long => Long] = logic ^^ { x =>
      t: Long => if (x(t)) 1 else 0
    }

    private def negLogic = negation ~ openBrace ~ logic ~ closeBrace ^^ {
    	case _ ~ _ ~ x ~ _ => t: Long => !x(t)
    }

    private def ternary: Parser[Long => Long] = (logic | negLogic) ~ "?" ~ subexpr ~ ":" ~ subexpr ^^ {
      case logic ~ _ ~ yes ~ _ ~ no => x: Long => if (logic(x)) yes(x) else no(x)
    }
}
