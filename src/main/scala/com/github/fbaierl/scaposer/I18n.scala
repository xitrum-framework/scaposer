package com.github.fbaierl.scaposer

object I18n {
  /**
   * Parsed [[Translation]]s contains comments, we strip them to reduce memory usage.
   * We don't need plural ID either as we only use (context, singular ID) as key
   * to lookup translated strings.
   */
  def apply(translations: Seq[Translation]): I18n = {
    // Strip unneccessary info and convert translations
    // to map of (ctx, singular) -> strs
    val ctxSingularToStrs = translations.foldLeft(
      Map.empty[(String, String), Seq[String]]
    ) { (acc, t) =>
      val key_value = t match {
        case SingularTranslation(_, ctx, _, singular, _, str, _) =>
          (ctx, singular) -> Seq(str)

        case PluralTranslation(_, ctx, _, singular, _, _, _, strs, _) =>
          (ctx, singular) -> strs
      }
      acc + key_value
    }

    I18n(ctxSingularToStrs)
  }
}

case class I18n(ctxSingularToStrs: Map[(String, String), Seq[String]]) {
  def ++(other: I18n): I18n = {
    // Note the order of "++", "other" will overwrite "this"
    val newMap = ctxSingularToStrs ++ other.ctxSingularToStrs

    // Ensure that pluralForms is not lost because "other" does not have it
    if (other.pluralFormso.isDefined) {
      I18n(newMap)
    } else {
      if (pluralFormso.isDefined) {
        val key   = ("", "")
        val value = ctxSingularToStrs(key)
        I18n(ctxSingularToStrs + (key -> value))
      } else {
        I18n(newMap)
      }
    }
  }

  def t(singular: String): String = lookupSingular("", singular)

  def tc(ctx: String, singular: String): String = lookupSingular(ctx, singular)

  def tn(singular: String, plural: String, n: Long): String = lookupPlural("", singular, plural, n)

  def tcn(ctx: String, singular: String, plural: String, n: Long): String = lookupPlural(ctx, singular, plural, n)

  override def toString = {
    val buffer = new StringBuilder
    buffer.append("\n")
    ctxSingularToStrs.foreach { case ((ctx, singular), strs) =>
      if (!ctx.isEmpty) {
        buffer.append(ctx)
        buffer.append("\n")
      }

      buffer.append(singular)
      buffer.append("\n")

      if (strs.size == 1) {
        buffer.append(strs.head)
        buffer.append("\n")
      } else {
        strs.zipWithIndex.foreach { case (str, index) =>
          buffer.append(index)
          buffer.append(" ")
          buffer.append(str)
          buffer.append("\n")
        }
      }
      buffer.append("\n")
    }
    buffer.toString
  }

  //----------------------------------------------------------------------------

  private def lookupSingular(ctx: String, singular: String): String = {
    ctxSingularToStrs.get((ctx, singular)) match {
      case Some(strs) =>
        // Newly created .pot/.po files have keys but the values are all empty
        val str = strs.head
        if (str.isEmpty) singular else str

      case None =>
        // Fallback
        if (ctx.isEmpty)
          singular
        else
          lookupSingular("", singular)  // Try translation without context
    }
  }

  private def lookupPlural(ctx: String, singular: String, plural: String, n: Long): String = {
    ctxSingularToStrs.get((ctx, singular)) match {
      case Some(strs) =>
        val index = evaluatePluralForms(n)
        if (index >= strs.size) {
          plural
        } else {
          // Newly created .pot/.po files have keys but the values are all empty
          val str = strs(index)
          if (str.isEmpty) singular else str
        }

      case None =>
        // Fallback
        if (ctx.isEmpty)
          if (n != 1) plural else singular       // English rule (not "n > 1"!)
        else
          lookupPlural("", singular, plural, n)  // Try translation without context
    }
  }

  //----------------------------------------------------------------------------

  // See evaluatePluralForms below
  private val pluralFormso: Option[String] = {
    ctxSingularToStrs.get(("", "")) match {
      case None => None

      case Some(strs) =>
        val header = strs.head
        //linesIterator is undeprecated in 2.13 to avoid ambiguity with lines method in JDK11
        header.linesIterator.find(_.startsWith("Plural-Forms")) match {
          case None => None

          case Some(line) =>
            Some(line.replaceFirst("^Plural-Forms:", "").replace(" ", ""))
        }
    }
  }

  // This evaluate method can only work correctly with Plural-Forms exactly listed at:
  // http://www.gnu.org/software/gettext/manual/html_node/Plural-forms.html#Plural-forms
  // http://www.gnu.org/software/gettext/manual/html_node/Translating-plural-forms.html#Translating-plural-forms
  private val evaluatePluralForms: (Long => Int) = {
    if (pluralFormsMatched("nplurals=1; plural=0")) {
      n => 0
    } else if (pluralFormsMatched("nplurals=2; plural=n != 1")) {
      n => if (n != 1) 1 else 0
    } else if (pluralFormsMatched("nplurals=2; plural=n>1")) {
      n => if (n > 1) 1 else 0
    } else if (pluralFormsMatched("nplurals=3; plural=n%10==1 && n%100!=11 ? 0 : n != 0 ? 1 : 2")) {
      n => if (n % 10 == 1 && n % 100 != 11) 0 else if (n != 0) 1 else 2
    } else if (pluralFormsMatched("nplurals=3; plural=n==1 ? 0 : n==2 ? 1 : 2")) {
      n => if (n == 1) 0 else if (n == 2) 1 else 2
    } else if (pluralFormsMatched("nplurals=3; plural=n==1 ? 0 : (n==0 || (n%100 > 0 && n%100 < 20)) ? 1 : 2")) {
      n => if (n == 1) 0 else if (n == 0 || (n % 100 > 0 && n % 100 < 20)) 1 else 2
    } else if (pluralFormsMatched("nplurals=3; plural=n%10==1 && n%100!=11 ? 0 : n%10>=2 && (n%100<10 || n%100>=20) ? 1 : 2")) {
      n => if (n % 10 == 1 && n % 100 != 11) 0 else if (n % 10 >= 2 && (n % 100 < 10 || n % 100 >= 20)) 1 else 2
    } else if (pluralFormsMatched("nplurals=3; plural=n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2")) {
      n => if (n % 10 == 1 && n % 100 != 11) 0 else if (n % 10 >= 2 && n % 10 <= 4 && (n % 100 < 10 || n % 100 >= 20)) 1 else 2
    } else if (pluralFormsMatched("nplurals=3; plural=(n==1) ? 0 : (n>=2 && n<=4) ? 1 : 2")) {
      n => if (n == 1) 0 else if (n >= 2 && n <= 4) 1 else 2
    } else if (pluralFormsMatched("nplurals=3; plural=n==1 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2")) {
      n => if (n == 1) 0 else if (n % 10 >= 2 && n % 10 <= 4 && (n % 100 < 10 || n % 100 >= 20)) 1 else 2
    } else if (pluralFormsMatched("nplurals=4; plural=n%100==1 ? 0 : n%100==2 ? 1 : n%100==3 || n%100==4 ? 2 : 3")) {
      n => if (n % 100 == 1) 0 else if (n % 100 == 2) 1 else if (n % 100 == 3 || n % 100 == 4) 2 else 3
    } else {
      pluralFormso.map { expr =>
        PluralIndexExpressionParser(expr) match {
          case PluralIndexExpressionParser.Success(result, _) => x: Long => result(x).toInt
          case failure: PluralIndexExpressionParser.NoSuccess => fallbackPluralFormEvaluator _
        }
      }.getOrElse(fallbackPluralFormEvaluator _)
    }
  }
  
  private def fallbackPluralFormEvaluator(n: Long) = 0

  private def pluralFormsMatched(pattern: String) = {
    pluralFormso match {
      case None              => false
      case Some(noSpaceLine) => noSpaceLine.indexOf(pattern.replace(" ", "")) >= 0
    }
  }
}
