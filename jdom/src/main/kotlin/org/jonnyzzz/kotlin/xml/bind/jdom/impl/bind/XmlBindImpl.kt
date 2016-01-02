package org.jonnyzzz.kotlin.xml.bind.jdom.impl.bind

import org.jdom2.CDATA
import org.jdom2.Element
import org.jdom2.Text
import org.jonnyzzz.kotlin.xml.bind.jdom.impl.JDOM

/**
 * Created by eugene.petrenko@gmail.com
 */
class XmlNameBind : XPropertyImpl<String>(null) {
  override fun loadImpl(scope: Element?) = scope?.name
  override fun saveImpl(value: String, scope: Element) {
    //NOP
  }
}

class XmlTextBind : XPropertyImpl<String>(null) {
  override fun loadImpl(scope: Element?) = scope?.text
  override fun saveImpl(value: String, scope: Element) {
    scope.setContent(Text(value))
  }
}

class XmlCDATABind : XPropertyImpl<String>(null) {
  override fun loadImpl(scope: Element?) = scope?.text
  override fun saveImpl(value: String, scope: Element) {
    scope.setContent(CDATA(value))
  }
}

class XmlUnknownBind : XPropertyImpl<Element>(null) {
  override fun loadImpl(scope: Element?) = scope?.clone()
  override fun saveImpl(value: Element, scope: Element) {
    val copy = value.clone()
    scope.addContent(copy.cloneContent())
    copy.attributes?.forEach { scope.setAttribute( it.clone() ) }
  }
}

class XmlAttributeBind(val attributeName : String) : XPropertyImpl<String>(null) {
  override fun loadImpl(scope: Element?) = scope?.getAttributeValue(attributeName, null as String?)
  override fun saveImpl(value: String, scope: Element) {
    scope.setAttribute(attributeName, value)
  }
}

class XmlSubBind<T : Any>(val clazz : Class<T>) : XPropertyImpl<T>(null), XmlBind {
  override fun loadImpl(scope: Element?): T? = if (scope == null) null else JDOM.load(scope, clazz)
  override fun saveImpl(value: T, scope: Element) {
    JDOM.save(scope, value)
  }
}
