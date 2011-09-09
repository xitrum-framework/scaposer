scaposer is a Gettext po file parser for Scala.

Basic usage
-----------

Use ``PoParser`` to parse and get a ``Po``:

::

  val string = """
  msgid "Hello"
  msgstr "Bonjour"
  """

  import scaposer.{Po, PoParser}
  val poo: Option[Po] = PoParser.parsePo(string)


Use "t" methods to get the translations:

::

  val po = poo.get
  po.t("Hello")

If there's no translation, the input is returned:

::

  po.t("Hi")

Context
-------

::

  val string = """
  msgctxt "Casual"
  msgid "Hello"
  msgstr "Salut"
  """

  val po = PoParser.parsePo(string).get
  po.t("Casual", "Hello")

If there's no translation for the context, the translation without context is tried.

Plural-Forms
------------

Your po file must define ``Plural-Forms`` exactly as at:

* http://www.gnu.org/software/gettext/manual/html_node/Plural-forms.html#Plural-forms
* http://www.gnu.org/software/gettext/manual/html_node/Translating-plural-forms.html#Translating-plural-forms

scaposer does not evaluate the ``plural`` expression (C language expression!),
it just performs string comparison!

::

  val string = """
  msgid ""
  msgstr "Plural-Forms: nplurals=2; plural=n>1;"

  msgid "I have one apple"
  msgid_plural "I have %d apples"
  msgstr[0] "J'ai une pomme"
  msgstr[1] "J'ai %d pommes"
  """

  val po = PoParser.parsePo(string).get
  po.t("I have one apple", "I have %d apples", 1)
  po.t("I have one apple", "I have %d apples", 2)
  po.t("A context", "I have one apple", "I have %d apples", 3)

== Use with SBT

::

  libraryDependencies += "tv.cntt" % "scaposer" % "1.0-SNAPSHOT"

scaposer is used in `Xitrum <https://github.com/ngocdaothanh/xitrum>`_.
