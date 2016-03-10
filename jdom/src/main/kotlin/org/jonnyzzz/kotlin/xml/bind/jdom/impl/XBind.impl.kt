package org.jonnyzzz.kotlin.xml.bind.jdom.impl

import org.jdom2.Element
import org.jonnyzzz.kotlin.xml.bind.*
import org.jonnyzzz.kotlin.xml.bind.jdom.impl.bind.XElementMatcher
import org.jonnyzzz.kotlin.xml.bind.jdom.impl.bind.XmlAnyElementWrap
import org.jonnyzzz.kotlin.xml.bind.jdom.impl.bind.XmlAnyElementsWrap
import org.jonnyzzz.kotlin.xml.bind.jdom.impl.bind.XmlBind
import java.util.*


internal class XMLAnyBuilderImpl(val elementsBefore : XMLBuilderImpl) : XMLAnyBinder {
  override fun <T : Any> div(t: XSub<T>) = elementsBefore.wrap( XmlAnyElementWrap( XMLBuilderImpl() / t ))
  override fun <T : Any> div(t: XUnknownElement<T>) = elementsBefore.wrap( XmlAnyElementWrap( XMLBuilderImpl() / t ))
}

internal class XMLsAnyBuilderImpl(val elementsBefore : XMLBuilderImpl) : XMLsAnyBinder {
  override fun <T : Any> div(t: XSub<T>) = elementsBefore.wrap( XmlAnyElementsWrap { XMLBuilderImpl() / t })
  override fun <T : Any> div(t: XUnknownElement<T>) = elementsBefore.wrap( XmlAnyElementsWrap { XMLBuilderImpl() / t })
}


internal object JDOMIMPL {
  val ROOT : XMLBuilderImpl
    get() = XMLBuilderImpl()

  fun <T : Any> copy(from : T, to : T) {
    val xml = save("mock-root-name", from)
    bind(xml, to)
  }

  fun <T : Any> clone(t : T) : T {
    val clazz = t.javaClass
    val xml = save("mock-root-name", t)
    return load(xml, clazz)
  }

  private fun <T : Any> elementsToBind(t : T): List<XmlBind> = t.delegatedProperties(XmlBind::class.java)

  fun <T : Any> load(_element : Element, clazz : Class<T>) : T = bind(_element, clazz.newInstance())

  fun <T : Any> bind(_element : Element, t : T) : T {
    val element = _element.clone()

    val fields = elementsToBind(t).toCollection(LinkedList())
    val p1: (XmlBind) -> Boolean = { it is XElementMatcher }

    fields.filter(p1).forEach { it.load(element) }
    fields.filter(p1).forEach {
      val matching = (it as XElementMatcher).matchingElements()
      element.removeChildren(matching)
    }
    fields.filterNot(p1).forEach { it.load(element) }
    return t
  }

  fun <T : Any> save(t : T, clazz : Class<T> = t.javaClass) : Element {
    val root = clazz.getAnnotationRec(XRoot::class.java) ?: throw RuntimeException("Failed to get name for $clazz. XRoot annotation is missing")
    val rootElementName = root.name
    return save(rootElementName, t)
  }

  fun <T : Any> save(rootElementName: String, t: T) : Element {
    val element = Element(rootElementName)
    save(element, t)
    return element
  }

  fun <T : Any> save(element: Element, t: T) {
    fun elementsToSave(): List<XmlBind> {
      val v = elementsToBind(t)
      if (!v.any{it.saveOrder != null}) return v
      return v.sortedBy { it -> (it.saveOrder ?: 0) }
    }

    elementsToSave().forEach {
      it.save { element }
    }
  }
}


