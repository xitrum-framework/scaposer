.. image:: poedit.png

Scaposer is a GNU gettext po file parser written in Scala.
It's strange that there's not many JVM libraries of this kind, see the
`discussion on Stackoverflow <http://stackoverflow.com/questions/4635721/is-there-a-java-library-for-parsing-gettext-po-files>`_.

To extract i18n strings from Scala source code files, use
`Scala xgettext <https://github.com/xitrum-framework/scala-xgettext>`_.

Presentation:
`I18nize Scala programs à la gettext <http://www.slideshare.net/ngocdaothanh/i18nize-scala-program-a-la-gettext>`_

Discussion group: https://groups.google.com/group/scala-xgettext

Basic usage
-----------

See `Scaladoc <http://xitrum-framework.github.io/scaposer/>`_.

::

  val po = """
  msgid "Hello"
  msgstr "Bonjour"
  """

  val result = scaposer.Parser.parse(po)
  // => An Either,
  // Left(scaposer.ParseFailure) or
  // Right(Seq[scaposer.Translation])

Use ``t`` methods to get the translations:

::

  val translations = result.right.get
  val i18n         = scaposer.I18n(translations)
  i18n.t("Hello")  // => "Bonjour"

If there's no translation, or the translation is an empty string
(not translated yet), the original input is returned:

::

  i18n.t("Hi")  // => "Hi"

Context
-------

::

  val po = """
  msgid "Hello"
  msgstr "Bonjour"

  msgctxt "Casual"
  msgid "Hello"
  msgstr "Salut"
  """

  val translations = scaposer.Parser.parse(po).right.get
  val i18n         = scaposer.I18n(translations)
  i18n.tc("Casual", "Hello")  // => "Salut"

If there's no translation for the context, the translation without context is tried:

::

  i18n.tc("Missing context", "Hello")  // => "Bonjour"


Plural-Forms
------------

::

  val po = """
  msgid ""
  msgstr "Plural-Forms: nplurals=2; plural=n>1;"

  msgid "I have one apple"
  msgid_plural "I have %d apples"
  msgstr[0] "J'ai une pomme"
  msgstr[1] "J'ai %d pommes"
  """

  val translations = scaposer.Parser.parse(po).right.get
  val i18n         = scaposer.I18n(translations)
  i18n.tn("I have one apple", "I have %d apples", 1)                // => "J'ai une pomme"
  i18n.tn("I have one apple", "I have %d apples", 2)                // => "J'ai 2 pommes"
  i18n.tcn("A context", "I have one apple", "I have %d apples", 3)  // => "J'ai 3 pommes"

For performance, your po file should define ``Plural-Forms`` exactly as at:

* http://www.gnu.org/software/gettext/manual/html_node/Plural-forms.html#Plural-forms
* http://www.gnu.org/software/gettext/manual/html_node/Translating-plural-forms.html#Translating-plural-forms

Otherwise, Scaposer cannot compare the plural form string, and it needs to parse and evaluate (slower).

Merge Po objects
----------------

You can merge multiple ``I18n``s together.

::

  val i18n4 = i18n1 ++ i18n2 ++ i18n3

Just like when you merge maps, translations in i18n3 will overwrite those in
i18n2 will overwrite those in i18n1.

Use with SBT
------------

Supported Scala versions: 2.11.x, 2.10.x

build.sbt example:

::

  libraryDependencies += "tv.cntt" %% "scaposer" % "1.10"

Scaposer is used in `Xitrum web framework <https://github.com/xitrum-framework/xitrum>`_.
