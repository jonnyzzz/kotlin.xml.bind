import org.jonnyzzz.kotlin.xml.bind.*
import org.jonnyzzz.kotlin.xml.bind.jdom.JDOM
import org.jonnyzzz.kotlin.xml.bind.jdom.XUnknown


class Example {
  var content by JDOM / "aaa" / "ggg" / XText
  var attribute by JDOM.saveOrder(5) / "aaa" / XAttribute("yohoho")
  var sub by JDOM / "sub" / XSub(Sub::class.java)

  var collection by JDOM / "parameters" / XElements("param") / "aaa" / XText
  var any by JDOM / "parameters" / XAnyElements / XSub(Sub::class.java)
}

class Sub {
  var key by JDOM[2] / XAttribute("key")
  var value by JDOM[1] / XAttribute("value") - "defaultValue"

  var unknown by JDOM / XUnknown
}

fun test() {
  val e = Example()
  e.sub = Sub()
}
