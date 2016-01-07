[![Build Status](https://travis-ci.org/jonnyzzz/kotlin.xml.bind.svg?branch=master)](https://travis-ci.org/jonnyzzz/kotlin.xml.bind)

Kotlin XML data binding DLS
==============

This is a tiny library that allows one to generate
XML files via Kotlin DSLs. The framework is build
upon delegated properties.

Project consists of API module with no dependencies
from any XML implementations.
There is an implementation module that implements
API via JDOM

License
-------

Apache 2.0

Binaries
========

You may download binaries from maven artifacts repository from here
http://dl.bintray.com/jonnyzzz/maven

In Gradle build script it may be done like this
```gradle
repositories {
  maven { url "http://dl.bintray.com/jonnyzzz/maven" }
}

dependencies {
  compile "org.jonnyzzz.kotlin.xml.bind:jdom:<VERSION>"
}
```

Usage example
-------------

```kotlin

class Example {
  //declares a property of type String that load data from "ROOT/aaa/ggg/text()"
  var content by JXML / "aaa" / "ggg" / XText

  //same, but for attribute value
  var attribute by JXML / "aaa" / XAttribute("yohoho")

  //parses sub-object fields
  var sub by JXML / "sub" / XSub(Sub::class.java)

  //collection (aka List<>) of strings is loaded here
  var collection by JXML / "parameters" / XElements("param") / "aaa" / XText

  //collection (aka List<>) of sub objects
  var any by JXML / "parameters" / XAnyElements / XSub(Sub::class.java)
}

class Sub {
  // [2] --- allows to define persist order
  var key by JXML[2] / XAttribute("key")

  // this specifies default value
  var value by JXML[1] / XAttribute("value") - "defaultValue"

  // sometimes a part of XML should be loaded as-is. Type is org.jdom.Element
  var unknown by JXML / XUnknown
}

///Usage examples
fun test() {
  val e = Example()

  /// save to XML
  val saved = JDOM.save(e)

  /// load from XML
  val copy = JDOM.load(saved, Example::class.java)
}
```
