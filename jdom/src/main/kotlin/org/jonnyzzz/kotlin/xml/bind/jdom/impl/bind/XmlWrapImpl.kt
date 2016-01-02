package org.jonnyzzz.kotlin.xml.bind.jdom.impl.bind

import org.jdom2.Content
import org.jdom2.Element
import org.jonnyzzz.kotlin.xml.bind.jdom.impl.delegatedProperties
import org.jonnyzzz.kotlin.xml.bind.jdom.impl.having

/**
 * Created by eugene.petrenko@gmail.com
 */

private fun <T : Any> anyElementSave(property : XBindProperty<T>, scope: () -> Element, getOrCreateElement : (Element, String) -> Element) {
  val v = property.value ?: return

  val elementName = if (v is Element) {
    v.name
  } else {
    val nameBind = v
            .delegatedProperties(XmlNameBind::class.java)
            .singleOrNull()
            ?: throw RuntimeException("AnyElement must have ROOT / XName in order to report it's name for persistence")

    nameBind.value ?: throw RuntimeException("AnyElement name must not be null")
  }

  property.save {
    getOrCreateElement(scope(), elementName)
  }
}

class XmlElementWrap<T : Any>(val elementName : String, property : XBindProperty<T>) : XPropertyDelegate<T>(property), XElementMatcher {
  override fun load(scope: Element?) {
    property.load(scope?.getChild(elementName))
  }

  override fun save(scope: () -> Element) {
    property.save {
      val el = scope();
      (el.getChild(elementName) ?: having(Element(elementName)) { el.addContent(this as Content) } )
    }
  }

  override fun matchingElements() = elementName
}

class XmlAnyElementWrap<T : Any>(property : XBindProperty<T>) : XPropertyDelegate<T>(property) {
  override fun load(scope: Element?) {
    property.load(scope?.getChildren()?.firstOrNull())
  }

  override fun save(scope: () -> Element) {
    anyElementSave(property, scope) { parent, elementName ->
      parent.getChild(elementName) ?: having(Element(elementName)) { parent.addContent(this as Content) }
    }
  }
}

class XmlElementsWrap<T : Any>(val elementName : String, val property : () -> XBindProperty<T>) : XPropertyImpl<List<T>>(null), XElementMatcher {
  override fun loadImpl(scope: Element?): List<T>? {
    if (scope == null) return null
    return scope.getChildren(elementName)?.map {
      val p = property()
      p.load(it);
      p.value
    }?.filterNotNull()
  }

  override fun saveImpl(value: List<T>, scope: Element) {
    value.forEach {
      val p = property()
      p.value = it
      p.save { having(Element(elementName)) { scope.addContent(this as Content) } }
    }
  }

  override fun matchingElements(): String = elementName
}

class XmlAnyElementsWrap<T : Any>(val property : () -> XBindProperty<T>) : XPropertyImpl<List<T>>(null) {
  override fun loadImpl(scope: Element?): List<T>? {
    if (scope == null) return null
    return scope.getChildren()?.map {
      val p = property()
      p.load(it)
      p.value
    }?.filterNotNull()
  }

  override fun saveImpl(value: List<T>, scope: Element) {
    value.forEach {
      val p = property()
      p.value = it
      anyElementSave(p, {scope}){ parent, elementName ->
        having(Element(elementName)) { parent.addContent(this as Content) }
      }
    }
  }
}
