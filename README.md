Kotlin XML DLS
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

Usage example
-------------

```kotlin

class Example {
  var content by JXML / "aaa" / "ggg" / XText
  var attribute by JXML.saveOrder(5) / "aaa" / XAttribute("yohoho")
  var sub by JXML / "sub" / XSub(Sub::class.java)

  var collection by JXML / "parameters" / XElements("param") / "aaa" / XText
  var any by JXML / "parameters" / XAnyElements / XSub(Sub::class.java)
}

class Sub {
  var key by JXML[2] / XAttribute("key")
  var value by JXML[1] / XAttribute("value") - "defaultValue"

  var unknown by JXML / XUnknown
}

fun test() {
  val e = Example()

  /// save to XML
  val saved = JDOM.save(e)

  /// load from XML
  val copy = JDOM.load(saved, Example::class.java)
}
```
