@file:Suppress("unused")
package org.jonnyzzz.kotlin.xml.bind.jdom

import org.jdom2.Element
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import org.jonnyzzz.kotlin.xml.bind.*
import org.jonnyzzz.kotlin.xml.bind.jdom.impl.JDOMIMPL
import org.jonnyzzz.kotlin.xml.dsl.XWriter
import org.jonnyzzz.kotlin.xml.dsl.jdom.jdom
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test


class XBindTest {
  @Test fun test_can_read_text_from_XML() {
    class Data {
      var X : String? by JXML / XText
    }

    val d : Data = JDOM.load(jdom("text") { text("hohoho")}, Data::class.java)
    Assert.assertEquals(d.X, "hohoho")
  }

  @Test fun test_can_read_text_from_XML_newlines() {
    class Data {
      var X : String? by JXML / XText
    }

    val d : Data = JDOM.load(jdom("text") { text("\nhohoho\n\n")}, Data::class.java)
    Assert.assertEquals(d.X, "\nhohoho\n\n")
  }

  @Test fun test_can_read_sub_text_from_XML() {
    class Data {
      var X : String? by JXML / "a" / "b" / XText
    }

    val d : Data = JDOM.load(jdom("text") { element("a") { element("b") { text("f777") } }; text("hohoho")}, Data::class.java)
    Assert.assertEquals(d.X, "f777")
  }

  @Test fun test_can_read_CDATA_from_XML() {
    class Data {
      var X : String? by JXML / XCDATA
    }

    val d : Data = JDOM.load(jdom("text") { cdata("hohoho")}, Data::class.java)
    Assert.assertEquals(d.X, "hohoho")
  }

  @Test fun test_can_read_CDATA_from_XML_newlines() {
    class Data {
      var X : String? by JXML / XCDATA
    }

    val d : Data = JDOM.load(jdom("text") { cdata("\nhohoho\n\n")}, Data::class.java)
    Assert.assertEquals(d.X, "\nhohoho\n\n")
  }

  @Test fun test_can_read_attribute_from_XML() {
    class Data {
      var X : String? by JXML / XAttribute("a")
    }

    val d : Data = JDOM.load(jdom("aaa") { attribute("a", "42")} , Data::class.java)

    Assert.assertEquals(d.X, "42")
  }

  @Test fun test_can_sub_read_attribute_from_XML() {
    class Data {
      var X : String? by JXML / "a" / XAttribute("a")
    }

    val d : Data = JDOM.load(jdom("aaa") {element("a") { attribute("a", "42")}} , Data::class.java)

    Assert.assertEquals(d.X, "42")
  }

  @Test fun test_can_read_sub_attribute_from_XML() {
    class Inner {
      var Y by JXML / "q" / XText
    }

    class Data {
      var X by JXML / "a" / XSub(Inner::class.java)
    }

    val d : Data = JDOM.load(jdom("aaa") {element("a") {element("q") { text("42")}}} , Data::class.java)
    Assert.assertEquals(d.X?.Y, "42")

    val d2 : Data = JDOM.load(jdom("aaa") {element("aZ") {element("q") { text("42")}}} , Data::class.java)
    Assert.assertNull(d2.X)
  }

  @Test fun test_can_read_unknown_attribute_from_XML() {
    class Data {
      var X by JXML / "a" / XUnknown
    }

    fun XWriter.q() = element("a") {element("q") { text("42")}}

    val d : Data = JDOM.load(jdom("aaa")  { q() } , Data::class.java)
    Assert.assertEquals(d.X.dump(), jdom("aaa") { q()}.getChild("a").dump())
  }


  @Test fun test_can_read_list_elements() {
    class Data {
      var X by JXML / "parameters" / XElements("param") / XAttribute("name")
    }

    val el = jdom("aaa") {
      element("parameters") {
        for(i in 1..5) {
          element("param") { attribute("name", "$i")}
        }
      }
    }

    val d : Data = JDOM.load(el , Data::class.java)
    Assert.assertEquals(d.X, listOf("1", "2", "3", "4", "5"))
  }

  @Test fun test_can_read_any_element() {
    class Inner {
      var N by JXML / XName
      var Y by JXML / XText
    }
    class Data {
      var X by JXML / "parameters" / XAnyElement / XSub(Inner::class.java)
    }

    val el = jdom("aaa") {
      element("parameters") {
        element("param") { text("yohoho")}
      }
    }

    val d : Data = JDOM.load(el , Data::class.java)
    Assert.assertEquals(d.X?.N, "param")
    Assert.assertEquals(d.X?.Y, "yohoho")
  }

  @Test fun test_can_read_any_elements() {
    class Inner {
      var N by JXML / XName
      var Y by JXML / XText
    }
    class Data {
      var X by JXML / "parameters" / XAnyElements / XSub(Inner::class.java)
    }

    val el = jdom("aaa") {
      element("parameters") {
        element("param") { text("yohoho")}
        element("qqq") { text("123")}
        element("ppp") { text("www")}
      }
    }

    val d : Data = JDOM.load(el , Data::class.java)
    Assert.assertEquals(d.X?.size, 3)
    Assert.assertEquals(d.X?.get(0)?.N, "param")
    Assert.assertEquals(d.X?.get(0)?.Y, "yohoho")
    Assert.assertEquals(d.X?.get(1)?.N, "qqq")
    Assert.assertEquals(d.X?.get(1)?.Y, "123")
    Assert.assertEquals(d.X?.get(2)?.N, "ppp")
    Assert.assertEquals(d.X?.get(2)?.Y, "www")
  }


  @Test fun test_read_any_element_does_not_include_parsed() {
    class Data {
      var X by JXML / "x" / XUnknown
      var Y by JXML / "Y" / XUnknown
      var Z by JXML / XAnyElements / XUnknown
    }

    val el = jdom("aaa") {
      element("x") { text("yohoho")}
      element("Y") { text("123")}
      element("z") { text("www")}
      element("p") { text("www")}
    }

    val d : Data = JDOM.load(el , Data::class.java)

    Assert.assertEquals(d.Z?.size, 2)
    Assert.assertEquals(d.Z?.get(0)?.name, "z")
    Assert.assertEquals(d.Z?.get(1)?.name, "p")
  }

  @Test
  @Ignore fun test_read_any_sub_element_does_not_include_parsed() {
    class Data {
      var X by JXML / "a" / "x" / XUnknown
      var Y by JXML / "a" / "Y" / XUnknown
      var Z by JXML / "a" / XAnyElements / XUnknown
    }

    val el = jdom("aaa") {
      element("a") {
        element("x") { text("yohoho") }
        element("Y") { text("123") }
        element("z") { text("www") }
        element("p") { text("www") }
      }
    }

    val d : Data = JDOM.load(el , Data::class.java)

    Assert.assertEquals(d.Z?.size, 2)
    Assert.assertEquals(d.Z?.get(0)?.name, "z")
    Assert.assertEquals(d.Z?.get(1)?.name, "p")
  }

  @Test fun test_read_ReadOnly() {
    class Data {
      var X by JXML / XText - XReadOnly
    }

    val el = jdom("aaa") {
      text("ddd")
    }

    val d : Data = JDOM.load(el , Data::class.java)

    Assert.assertEquals(d.X, "ddd")
  }

  @Test fun test_read_onLoaded() {
    class Data {
      var X by JXML / XText - XReadOnly - XCallback<String>(onLoaded = { a -> (a ?: "") + "_OnLoaded"})
    }

    val el = jdom("aaa") {
      text("ddd")
    }

    val d : Data = JDOM.load(el , Data::class.java)

    Assert.assertEquals(d.X, "ddd_OnLoaded")
  }

  @Test fun test_read_XNameList() {
    class Data {
      var Name by JXML / XName
      var Value by JXML / XAttribute("x")
    }

    class Values {
      val Foo by JXML / "r" / XAnyElements / XSub(Data::class.java)
    }

    val el = jdom("aaa") {
      element("r") {
        element("a") {
          attribute("x", "aq")
        }
        element("a") {
          attribute("x", "ab")
        }
        element("b") {
          attribute("x", "bb")
        }
        element("c") {
          attribute("x", "cc")
        }
        element("c") {
          attribute("x", "rr")
        }
      }
    }

    val d  = JDOM.load(el , Values::class.java)

    println(d)
    Assert.assertEquals(d.Foo?.size, 5)

    Assert.assertEquals(d.Foo?.get(0)?.Name, "a")
    Assert.assertEquals(d.Foo?.get(0)?.Value, "aq")

    Assert.assertEquals(d.Foo?.get(1)?.Name, "a")
    Assert.assertEquals(d.Foo?.get(1)?.Value, "ab")

    Assert.assertEquals(d.Foo?.get(2)?.Name, "b")
    Assert.assertEquals(d.Foo?.get(2)?.Value, "bb")

    Assert.assertEquals(d.Foo?.get(3)?.Name, "c")
    Assert.assertEquals(d.Foo?.get(3)?.Value, "cc")

    Assert.assertEquals(d.Foo?.get(4)?.Name, "c")
    Assert.assertEquals(d.Foo?.get(4)?.Value, "rr")

    saveLoadTest(d, { Foo?.flatMap { listOf("z", "n" + it.Name, "v" + it.Value) } ?: listOf()})
  }


  @Test fun test_can_write_text_from_XML() {
    class Data {
      var X : String? by JXML / XText
    }

    val d = Data().apply { X = "42"}
    val element = JDOMIMPL.save("aaa", d)

    Assert.assertEquals(element.name, "aaa")
    Assert.assertEquals(element.textTrim, "42")
  }

  @Test fun test_can_write_sub_text_from_XML() {
    class Data {
      var X : String? by JXML / "q" / XText
    }

    val d = Data().apply { X = "42"}
    val element = JDOMIMPL.save("aaa", d)

    Assert.assertEquals(element.name, "aaa")
    Assert.assertEquals(element.getChild("q")?.textTrim, "42")
  }

  @Test fun test_can_write_cdata_from_XML() {
    class Data {
      var X : String? by JXML / XCDATA
    }

    val d = Data().apply { X = "42"}
    val element = JDOMIMPL.save("aaa", d)

    Assert.assertEquals(element.name, "aaa")
    Assert.assertEquals(element.textTrim, "42")
  }

  @Test fun test_can_write_cdata_from_XML_newline() {
    class Data {
      var X : String? by JXML / XCDATA
    }

    val d = Data().apply { X = "\n\n42\n\n"}
    val element = JDOMIMPL.save("aaa", d)

    Assert.assertEquals(element.name, "aaa")
    Assert.assertEquals(element.text, "\n\n42\n\n")
  }

  @Test fun test_can_write_attribute_from_XML() {
    class Data {
      var X : String? by JXML / XAttribute("a")
    }

    val d = Data().apply { X = "42"}
    val element = JDOMIMPL.save("aaa", d)

    Assert.assertEquals(element.name, "aaa")
    Assert.assertEquals(element.getAttributeValue("a"), "42")
  }

  @Test fun test_can_sub_write_attribute_from_XML() {
    class Data {
      var X : String? by JXML / "bbb"/ XAttribute("a")
    }

    val d = Data().apply { X = "42"}
    val element = JDOMIMPL.save("aaa", d)

    Assert.assertEquals(element.name, "aaa")
    Assert.assertEquals(element.getChild("bbb")?.getAttributeValue("a"), "42")
  }

  @Test fun test_can_sub_write_attribute_in_order() {
    class Data {
      var A by JXML[2] / XAttribute("a") - "a"
      var B by JXML[1] / XAttribute("b") - "b"
      var C by JXML[3] / XAttribute("c") - "c"
    }

    val d = Data().apply {  }
    val element = JDOMIMPL.save("aaa", d)

    Assert.assertEquals(element.name, "aaa")
    Assert.assertEquals(element.attributes?.size, 3)
    Assert.assertEquals(element.attributes?.get(0)?.name, "b")
    Assert.assertEquals(element.attributes?.get(1)?.name, "a")
    Assert.assertEquals(element.attributes?.get(2)?.name, "c")
  }

  @Test fun test_can_sub_write_attribute_in_order_inherit() {
    open class DataBase {
      var B by JXML[1] / XAttribute("b") - "b"
    }
    open class DataInner : DataBase()
    class Data : DataInner() {
      var A by JXML[2] / XAttribute("a") - "a"
      var C by JXML[3] / XAttribute("c") - "c"
    }

    val d = Data().apply {  }
    val element = JDOMIMPL.save("aaa", d)

    Assert.assertEquals(element.name, "aaa")
    Assert.assertEquals(element.attributes?.size, 3)
    Assert.assertEquals(element.attributes?.get(0)?.name, "b")
    Assert.assertEquals(element.attributes?.get(1)?.name, "a")
    Assert.assertEquals(element.attributes?.get(2)?.name, "c")
  }

  @Test fun test_can_sub_write_elements_in_order_inherit() {
    open class DataBase {
      var B by JXML[1] / "b" / XText - "b"
    }
    open class DataInner : DataBase()
    class Data : DataInner() {
      var A by JXML[2] / "a" / XText - "a"
      var C by JXML[3] / "c" / XText - "c"
    }

    val d = Data().apply {  }
    val element = JDOMIMPL.save("aaa", d)

    println(element.dump())

    Assert.assertEquals(element.name, "aaa")
    Assert.assertEquals(element.children?.size, 3)
    Assert.assertEquals(element.children?.get(0)?.name, "b")
    Assert.assertEquals(element.children?.get(1)?.name, "a")
    Assert.assertEquals(element.children?.get(2)?.name, "c")
  }

  @Test fun test_can_write_sub_attribute_from_XML() {
    class Inner {
      var X : String? by JXML / "bbb" / XAttribute("a")
    }
    class Data {
      var I by JXML / "zzz"/ XSub(Inner::class.java)
    }

    val d = Data().apply { I = Inner().apply{X = "42"}}
    val element = JDOMIMPL.save("aaa", d)

    println(element.dump())

    Assert.assertEquals(element.name, "aaa")
    Assert.assertEquals(element.getChild("zzz")?.getChild("bbb")?.getAttributeValue("a"), "42")
  }


  @Test fun test_should_not_add_inner_elements_for_null() {
    class Inner {
      var X : String? by JXML / "bbb" / "ccc" / "ddd" / XAttribute("a")
    }

    val d = Inner()
    val element = JDOMIMPL.save("aaa", d)

    println(element.dump())

    Assert.assertEquals(element.name, "aaa")
    Assert.assertNull(element.getChild("bbb")?.getChild("ccc")?.getChild("ddd")?.getAttributeValue("a"))
    Assert.assertNull(element.getChild("bbb")?.getChild("ccc")?.getChild("ddd"))
    Assert.assertNull(element.getChild("bbb")?.getChild("ccc"))
    Assert.assertNull(element.getChild("bbb"))
  }

  @Test fun test_can_write_list_elements() {
    class Data {
      var X by JXML / "parameters" / XElements("param") / XAttribute("name")
    }

    val d = Data().apply { X = listOf("a", "b", "c", "d")}
    val element = JDOMIMPL.save("aaa", d)

    println(element.dump())

    Assert.assertEquals(element.name, "aaa")
    Assert.assertNotNull(element.getChild("parameters"))
    Assert.assertEquals(element.getChild("parameters")?.children?.size, 4)
    Assert.assertEquals(element.getChild("parameters")?.children?.map{it.getAttributeValue("name")}, listOf("a", "b", "c", "d"))
  }

  @Test fun test_write_element_default_value() {
    class Data {
      var X by JXML / XAttribute("name") - "42"
    }

    val d = Data().apply {  }
    val element = JDOMIMPL.save("aaa", d)

    println(element.dump())

    Assert.assertEquals(element.name, "aaa")
    Assert.assertNotNull(element.getAttributeValue("name"), "42")
  }

  @Test fun test_write_any_element() {
    class Inner {
      var N by JXML / XName
      var Y by JXML / XText
    }
    class Data {
      var X by JXML / "parameters" / XAnyElement / XSub(Inner::class.java)
    }

    val d = Data().apply { X = Inner().apply { N = "qqq"; Y = "test" }}
    val element = JDOMIMPL.save("aaa", d)
    println(element.dump())

    Assert.assertEquals(element.name, "aaa")
    Assert.assertEquals(element.getChild("parameters")?.getChild("qqq")?.textTrim, "test")
  }

  @Test fun test_write_any_element_empty() {
    class Inner {
      var N by JXML / XName
      var Y by JXML / XText
    }
    class Data {
      var X by JXML / "parameters" / XAnyElement / XSub(Inner::class.java)
    }

    val d = Data().apply { }
    val element = JDOMIMPL.save("aaa", d)
    println(element.dump())

    Assert.assertEquals(element.name, "aaa")
    Assert.assertEquals(element.getChild("parameters"), null)
  }

  @Test fun test_write_any_elements() {
    class Inner {
      var N by JXML / XName
      var Y by JXML / XText
    }
    class Data {
      var X by JXML / "parameters" / XAnyElements / XSub(Inner::class.java)
    }

    val d = Data().apply {
      X = listOf(
              Inner().apply { N = "qqq"; Y = "test" },
              Inner().apply { N = "eee"; Y = "zzz" })
    }
    val element = JDOMIMPL.save("aaa", d)
    println(element.dump())

    Assert.assertEquals(element.name, "aaa")
    Assert.assertEquals(element.getChild("parameters")?.getChild("qqq")?.textTrim, "test")
    Assert.assertEquals(element.getChild("parameters")?.getChild("eee")?.textTrim, "zzz")
  }

  @Test fun test_write_ReadOnly() {
    class Inner {
      var N by JXML / XName
      var Y by JXML / XText
    }
    class Data {
      var X by JXML / "parameters" / XAnyElements / XSub(Inner::class.java) - XReadOnly
    }

    val d = Data().apply {
      X = listOf(
              Inner().apply { N = "qqq"; Y = "test" },
              Inner().apply { N = "eee"; Y = "zzz" })
    }
    val element = JDOMIMPL.save("aaa", d)
    println(element.dump())

    Assert.assertEquals(element.name, "aaa")
    Assert.assertTrue(element.children?.isEmpty() ?: false)
  }

  @Test fun test_write_any_elements_empty() {
    class Inner {
      var N by JXML / XName
      var Y by JXML / XText
    }
    class Data {
      var X by JXML / "parameters" / XAnyElements / XSub(Inner::class.java)
    }

    val d = Data().apply {     }
    val element = JDOMIMPL.save("aaa", d)
    println(element.dump())

    Assert.assertEquals(element.name, "aaa")
    Assert.assertEquals(element.getChild("parameters"), null)
  }

  @Test fun test_write_onBeforeSave() {
    class Data {
      var X : String? by JXML / XText - XCallback<String>(onBeforeSave = { X -> (X ?: "") + "_OnBeforeSaved"})
    }

    val d = Data().apply {  X = "s"   }
    val element = JDOMIMPL.save("aaa", d)
    println(element.dump())

    Assert.assertEquals(element.name, "aaa")
    Assert.assertEquals(element.text, "s_OnBeforeSaved")
  }


  @Test fun test_load_save_1() {
    class Data {
      var X : String? by JXML / XCDATA
      var A : String? by JXML / XAttribute("a")
      var B : String? by JXML / "b" / XAttribute("a")
    }

    val d = Data().apply { X = "xxx"; A = "qqq"; B = "zzz"}
    saveLoadTest(d) { listOf(X, A, B) }
  }

  @Test fun test_load_save_2() {
    class Data {
      var X : String? by JXML / XCDATA
      var A : String? by JXML / XAttribute("a")
      var B : String? by JXML / "b" / XAttribute("a")
    }

    val d = Data().apply { X = "xxx"; A = null; B = "zzz"}
    saveLoadTest(d) { listOf(X, A, B) }
  }

  @Test fun test_load_save_3_sub() {
    class Inner {
      var X : String? by JXML / XCDATA
      var A : String? by JXML / XAttribute("a")
      var B : String? by JXML / "b" / XAttribute("a")
    }

    class Data {
      var i1 by JXML / "a1" / XSub(Inner::class.java)
      var i2 by JXML / "a2" / XSub(Inner::class.java)
      var i3 by JXML / "a3" / XSub(Inner::class.java)
    }

    val d = Data().apply { i1 = Inner().apply{X = "xxx"; A = null; B = "zzz"}; i3 = Inner().apply{X = "333"; A = null; B = "z333zz"}; }
    saveLoadTest(d) { listOf(i1?.X, i1?.A, i1?.B, i2?.X, i2?.A, i2?.B, i3?.X, i3?.A, i3?.B) }
  }

  @Test fun test_load_save_4_list() {
    class Inner {
      var name by JXML / XAttribute("name")
      var value by JXML / XAttribute("value")
    }

    class Data {
      var p by JXML / XElements("param") / XSub(Inner::class.java)
    }

    val d = Data().apply {
      p = listOf(
              Inner().apply { name = "a"; value = "b" },
              Inner().apply { name = "c"; value = "d" },
              Inner().apply { name = "e"; value = "F" }
      )
    }

    saveLoadTest(d) { listOf("mock") + (p?.flatMap { listOf(it.name, it.value) } ?: listOf()) }
  }

  @Test fun test_load_save_5_unknown() {
    class Data {
      var value by JXML / XAnyElement / XUnknown
    }

    val d = Data().apply {
      value = jdom("test") { element("qq"); attribute("a", "b")}
    }

    saveLoadTest(d) { listOf(value.dump()) }
  }

  @Test fun test_load_save_6_unknown() {
    class Data {
      var value by JXML / XAnyElements / XUnknown
    }

    val d = Data().apply {
      value = listOf(
              jdom("test") { element("qq"); attribute("a", "b")},
              jdom("test2") { element("qq"); attribute("a", "b")},
              jdom("te") { element("qsq"); attribute("a", "b")}
      )
    }

    saveLoadTest(d) { listOf("32") + (value?.map{it.dump()} ?: listOf()) }
  }

  @Test fun test_load_save_7_multiline_cdata() {
    class Data {
      var value by JXML / XCDATA
    }

    val d = Data().apply {
      value = "this\nis\r\na\nmultiline"
    }

    saveLoadTest(d) { listOf("32") + listOf(value) }
  }

  @Test fun test_load_save_8_multiline_text() {
    class Data {
      var value by JXML / XText
    }

    val d = Data().apply {
      value = "this\nis\r\na\nmultiline"
    }

    saveLoadTest(d) { listOf("32") + listOf(value) }
  }


  private fun <Y : Any> saveLoadTest(d : Y, hash : Y.() -> List<Any?>) {
    val element = JDOMIMPL.save("aaa", d)
    val n = JDOM.load(element, d.javaClass)

    val l1 = d.hash()
    val l2 = n.hash()

    println(element.dump())

    Assert.assertEquals(l1, l2)
  }

  private fun Element?.dump() : String {
    if (this == null) return "<null>"
    return XMLOutputter().apply { format = Format.getPrettyFormat() }.outputString(this)!!
  }

}