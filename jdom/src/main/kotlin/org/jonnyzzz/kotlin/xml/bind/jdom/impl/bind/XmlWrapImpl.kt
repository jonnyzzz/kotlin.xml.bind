package org.jonnyzzz.kotlin.xml.bind.jdom.impl.bind

import org.jdom2.Content
import org.jdom2.Element
import org.jonnyzzz.kotlin.xml.bind.jdom.impl.delegatedProperties

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

internal class XmlElementWrap<T : Any>(val elementName : String, property : XBindProperty<T>) : XPropertyDelegate<T>(property), XElementMatcher {
  override fun load(scope: Element?) {
    property.load(scope?.getChild(elementName))
  }

  override fun save(scope: () -> Element) {
    property.save {
      val el = scope();
      (el.getChild(elementName) ?: Element(elementName).apply { el.addContent(this as Content) } )
    }
  }

  override fun matchingElements() = elementName
}

internal class XmlAnyElementWrap<T : Any>(property : XBindProperty<T>) : XPropertyDelegate<T>(property) {
  override fun load(scope: Element?) {
    property.load(scope?.children?.firstOrNull())
  }

  override fun save(scope: () -> Element) {
    anyElementSave(property, scope) { parent, elementName ->
      parent.getChild(elementName) ?: Element(elementName).apply { parent.addContent(this as Content) }
    }
  }
}

internal class XmlElementsWrap<T : Any>(val elementName : String, val property : () -> XBindProperty<T>) : XPropertyImpl<List<T>>(null), XElementMatcher {
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
      p.save { Element(elementName).apply { scope.addContent(this as Content) } }
    }
  }

  override fun matchingElements(): String = elementName
}

internal class XmlAnyElementsWrap<T : Any>(val property : () -> XBindProperty<T>) : XPropertyImpl<List<T>>(null) {
  override fun loadImpl(scope: Element?): List<T>? {
    if (scope == null) return null
    return scope.children?.map {
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
        Element(elementName).apply { parent.addContent(this as Content) }
      }
    }
  }
}
