
import org.jonnyzzz.kotlin.xml.bind.*
import org.jonnyzzz.kotlin.xml.bind.jdom.JDOM
import org.jonnyzzz.kotlin.xml.bind.jdom.JXML
import org.jonnyzzz.kotlin.xml.bind.jdom.XUnknown


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
