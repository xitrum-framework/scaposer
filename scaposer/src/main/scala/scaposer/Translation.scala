package scaposer

sealed trait Translation

/**
 * Represents a singular form translation item parsed from .po file.
 * Empty [[ctx]] means there's no ctx specified.
 * Emptry comments means there are no comments specified.
 */
case class SingularTranslation(
  ctxComments: Seq[String],
  ctx: String,
  singularComments: Seq[String],
  singular: String,
  strComments: Seq[String],
  str: String,
  otherComments: Seq[String]
) extends Translation

/**
 * Represents a singular form translation item parsed from .po file.
 * Empty [[ctx]] means there's no ctx specified.
 * Emptry comments means there are no comments specified.
 */
case class PluralTranslation(
  ctxComments: Seq[String],
  ctx: String,
  singularComments: Seq[String],
  singular: String,
  pluralComments: Seq[String],
  plural: String,
  strsComments: Seq[String],
  strs: Seq[String],
  otherComments: Seq[String]
) extends Translation
