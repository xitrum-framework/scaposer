package scaposer

/**
 * Represents a translation item.
 * `msgid_plural` in po file is for translators. We do not need to store it here
 * because it is available in the translation methods of class [[Po]].
 */
case class Translation(ctxo: Option[String], singular: String, strs: Seq[String])
