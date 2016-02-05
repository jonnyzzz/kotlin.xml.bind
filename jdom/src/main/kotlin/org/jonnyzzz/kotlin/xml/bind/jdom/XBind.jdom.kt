package org.jonnyzzz.kotlin.xml.bind.jdom

import org.jdom2.Element
import org.jonnyzzz.kotlin.xml.bind.XMLRootBuilder
import org.jonnyzzz.kotlin.xml.bind.XUnknownElement
import org.jonnyzzz.kotlin.xml.bind.jdom.impl.JDOMIMPL

object XUnknown : XUnknownElement<Element>(Element::class.java)

interface JDOMXMLRootBuilder {
  fun <T : Any> load(element: Element, clazz: Class<T>): T
  fun <T : Any> save(t: T, clazz: Class<T> = t.javaClass): Element

  fun <T : Any> clone(t: T): T
}

///root object for app bindings, data factory
val JXML : XMLRootBuilder
   get() = JDOMIMPL.ROOT

object JDOM : JDOMXMLRootBuilder {
  override fun <T : Any> load(element: Element, clazz: Class<T>): T = JDOMIMPL.load(element, clazz)
  override fun <T : Any> save(t: T, clazz: Class<T>): Element = JDOMIMPL.save(t, clazz)
  override fun <T : Any> clone(t: T): T = JDOMIMPL.clone(t)
}
