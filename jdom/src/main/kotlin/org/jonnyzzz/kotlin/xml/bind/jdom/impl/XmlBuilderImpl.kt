package org.jonnyzzz.kotlin.xml.bind.jdom.impl

import org.jdom2.Element
import org.jonnyzzz.kotlin.xml.bind.*
import org.jonnyzzz.kotlin.xml.bind.jdom.impl.bind.*

/**
 * Created by eugene.petrenko@gmail.com
 */
class XMLBuilderImpl(val elementNames : List<String> = listOf(),
                     val saveOrder : Int? = null) : XMLRootBuilder {
  override fun div(element: String) = XMLBuilderImpl(elementNames + listOf(element), saveOrder)
  override fun div(element: XAnyElement) = XMLAnyBuilderImpl(this)

  override fun div(element: XElements) = XMLsBuilderImpl(this, element.name)
  override fun div(element: XAnyElements) = XMLsAnyBuilderImpl(this)

  override fun div(t: XName) = wrap(XmlNameBind())
  override fun div(t: XText) = wrap(XmlTextBind())
  override fun div(t: XCDATA) = wrap(XmlCDATABind())
  override fun div(t: XAttribute) = wrap(XmlAttributeBind(t.name))

  override fun <T : Any> div(t: XSub<T>)  = wrap(XmlSubBind(t.clazz))

  override fun <T : Any> div(t: XUnknownElement<T>) : XBindProperty<T> {
    if (t.clazz != Element::class.java) throw Error("Only JDOM Element is supported")

    @Suppress("UNCHECKED_CAST")
    return wrap(XmlUnknownBind()) as XBindProperty<T>
  }

  fun <T : Any> wrap(p : XBindProperty<T>, elements : Iterable<String> = elementNames) : XBindProperty<T> {
    val x = when {
      elements.none() -> p
      else -> XmlElementWrap(elements.first(), wrap(p, elements.drop(1)))
    }
    x.saveOrder = saveOrder
    return x
  }

  override fun saveOrder(order: Int): XMLBuilder = XMLBuilderImpl(elementNames, order)
}
