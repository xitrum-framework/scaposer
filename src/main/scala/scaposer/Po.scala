package scaposer

// See class Translation.
// body is a map of (ctxo, singular) -> strs
class Po(val body: Map[(Option[String], String), Array[String]]) {
  def ++(other: Po): Po = {
    // Note the order of "++", other" will overwrite "this"
    val newBody = body ++ other.body

    // Ensure that pluralForms is not lost because "other" does not have it
    if (other.pluralFormso.isDefined) {
      new Po(newBody)
    } else {
      if (pluralFormso.isDefined) {
	    val key   = (None, "")
	    val value = body(key)
	    new Po(body + (key -> value))
	  } else {
	    new Po(newBody)
	  }
    }
  }

  def t(singular: String): String = lookupSingular(None, singular)

  def t(ctx: String, singular: String): String = lookupSingular(Some(ctx), singular)

  def t(singular: String, plural: String, n: Long): String = lookupPlural(None, singular, plural, n)

  def t(ctx: String, singular: String, plural: String, n: Long): String = lookupPlural(Some(ctx), singular, plural, n)

  override def toString = {
    val buffer = new StringBuilder
    buffer.append("\n")
    body.foreach { case ((ctxo, singular), strs) =>
      if (!ctxo.isEmpty) {
        buffer.append(ctxo.get)
        buffer.append("\n")
      }

      buffer.append(singular)
      buffer.append("\n")

      if (strs.size == 1) {
        buffer.append(strs(0))
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

  private def lookupSingular(ctxo: Option[String], singular: String): String = {
    body.get((ctxo, singular)) match {
      case Some(strs) => strs(0)

      case None =>
        if (ctxo.isDefined)
          lookupSingular(None, singular)  // Try translation without context
        else
          singular
    }
  }

  private def lookupPlural(ctxo: Option[String], singular: String, plural: String, n: Long): String = {
    body.get((ctxo, singular)) match {
      case Some(strs) =>
        val index = evaluatePluralForms(n)
        if (index >= strs.size) plural else strs(index)

      case None =>
        if (ctxo.isDefined)
          lookupPlural(None, singular, plural, n)  // Try translation without context
        else
          if (n != 1) plural else singular  // English rule
    }
  }

  //----------------------------------------------------------------------------

  // See evaluatePluralForms below
  private val pluralFormso: Option[String] = {
    body.get((None, "")) match {
      case None => None

      case Some(strs) =>
        val header = strs(0)
        header.lines.find(_.startsWith("Plural-Forms")) match {
          case None => None

          case Some(line) =>
            Some(line.replace(" ", ""))
        }
    }
  }

  // This evaluate method can only work correctly with Plural-Forms exactly listed at:
  // http://www.gnu.org/software/gettext/manual/html_node/Plural-forms.html#Plural-forms
  // http://www.gnu.org/software/gettext/manual/html_node/Translating-plural-forms.html#Translating-plural-forms
  private def evaluatePluralForms(n: Int):  Int = evaluatePluralForms(n.toLong)
  private def evaluatePluralForms(n: Long): Int = {
    if (pluralFormsMatched("nplurals=1; plural=0")) {
      0
    } else if (pluralFormsMatched("nplurals=2; plural=n != 1")) {
      if (n != 1) 1 else 0
    } else if (pluralFormsMatched("nplurals=2; plural=n>1")) {
      if (n > 1) 1 else 0
    } else if (pluralFormsMatched("nplurals=3; plural=n%10==1 && n%100!=11 ? 0 : n != 0 ? 1 : 2")) {
      if (n % 10 == 1 && n % 100 != 11) 0 else if (n != 0) 1 else 2
    } else if (pluralFormsMatched("nplurals=3; plural=n==1 ? 0 : n==2 ? 1 : 2")) {
      if (n == 1) 0 else if (n == 2) 1 else 2
    } else if (pluralFormsMatched("nplurals=3; plural=n==1 ? 0 : (n==0 || (n%100 > 0 && n%100 < 20)) ? 1 : 2")) {
      if (n == 1) 0 else if (n == 0 || (n % 100 > 0 && n % 100 < 20)) 1 else 2
    } else if (pluralFormsMatched("nplurals=3; plural=n%10==1 && n%100!=11 ? 0 : n%10>=2 && (n%100<10 || n%100>=20) ? 1 : 2")) {
      if (n % 10 == 1 && n % 100 != 11) 0 else if (n % 10 >= 2 && (n % 100 < 10 || n % 100 >= 20)) 1 else 2
    } else if (pluralFormsMatched("nplurals=3; plural=n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2")) {
      if (n % 10 == 1 && n % 100 != 11) 0 else if (n % 10 >= 2 && n % 10 <= 4 && (n % 100 < 10 || n % 100 >= 20)) 1 else 2
    } else if (pluralFormsMatched("nplurals=3; plural=(n==1) ? 0 : (n>=2 && n<=4) ? 1 : 2")) {
      if (n == 1) 0 else if (n >= 2 && n <= 4) 1 else 2
    } else if (pluralFormsMatched("nplurals=3; plural=n==1 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2")) {
      if (n == 1) 0 else if (n % 10 >= 2 && n % 10 <= 4 && (n % 100 < 10 || n % 100 >= 20)) 1 else 2
    } else if (pluralFormsMatched("nplurals=4; plural=n%100==1 ? 0 : n%100==2 ? 1 : n%100==3 || n%100==4 ? 2 : 3")) {
      if (n % 100 == 1) 0 else if (n % 100 == 2) 1 else if (n % 100 == 3 || n % 100 == 4) 2 else 3
    } else {
      0
    }
  }

  private def pluralFormsMatched(pattern: String) = {
    pluralFormso match {
      case None              => false
      case Some(noSpaceLine) => noSpaceLine.indexOf(pattern.replace(" ", "")) >= 0
    }
  }
}
