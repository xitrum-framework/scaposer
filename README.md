[![Build Status](https://travis-ci.org/fbaierl/scalajs-scaposer.svg?branch=master)](https://travis-ci.org/fbaierl/scalajs-scaposer) 
[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-0.6.17.svg)](https://www.scala-js.org)

# scalajs-scaposer

[Scala.js](https://www.scala-js.org/) version of [scaposer](https://github.com/xitrum-framework/scaposer).

# Basic Usage

See also: [xitrum-framework Scaladoc](http://xitrum-framework.github.io/scaposer/)

```scala
val po = """
msgid "Hello"
msgstr "Bonjour"
"""

val result = scaposer.Parser.parse(po)
// => An Either,
// Left(scaposer.ParseFailure) or
// Right(Seq[scaposer.Translation])
```

Use `t` methods to get the translations:

```scala
val translations = result.right.get
val i18n         = scaposer.I18n(translations)
i18n.t("Hello")  // => "Bonjour"
```

If there's no translation, or the translation is an empty string
(not translated yet), the original input is returned:

```scala
  i18n.t("Hi")  // => "Hi"
```

## Context

```scala
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
```

If there's no translation for the context, the translation without context is tried:

```scala
  i18n.tc("Missing context", "Hello")  // => "Bonjour"
```

## Plural-Forms

```scala
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
i18n.tn("I have one apple", "I have %d apples", 2)                // => "J'ai %d pommes"
i18n.tcn("A context", "I have one apple", "I have %d apples", 3)  // => "J'ai %d pommes"
```

For performance, your po file should define `Plural-Forms` exactly as at:

* http://www.gnu.org/software/gettext/manual/html_node/Plural-forms.html#Plural-forms
* http://www.gnu.org/software/gettext/manual/html_node/Translating-plural-forms.html#Translating-plural-forms

Otherwise, Scaposer cannot compare the plural form string, and it needs to parse and evaluate (slower).

## Merge Po objects

You can merge multiple `I18n`s together.

```scala
val i18n4 = i18n1 ++ i18n2 ++ i18n3
```

Just like when you merge maps, translations in i18n3 will overwrite those in
i18n2 will overwrite those in i18n1.

# Installation

Supported Scala versions: 2.12.x, 2.11.x, 2.10.x

build.sbt example:

```scala
libraryDependencies += "com.github.fbaierl" %%% "scalajs-scaposer" % "0.1.2"
```