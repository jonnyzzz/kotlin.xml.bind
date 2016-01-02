package org.jonnyzzz.kotlin.xml.bind.jdom.impl

import org.jonnyzzz.kotlin.xml.bind.*
import org.jonnyzzz.kotlin.xml.bind.jdom.impl.bind.XBindProperty
import org.jonnyzzz.kotlin.xml.bind.jdom.impl.bind.XmlElementsWrap

/**
 * Created by eugene.petrenko@gmail.com
 */
class XMLsBuilderImpl(val elementsBefore : XMLBuilderImpl,
                      val elementAsList : String,
                      val elementsAfter : List<String> = listOf()) : XMLsBuilder {
  override fun div(element: String) = XMLsBuilderImpl(elementsBefore, elementAsList, elementsAfter + listOf(element))

  override fun div(t: XName) = wrap { this / t }
  override fun div(t: XText) = wrap { this / t }
  override fun div(t: XCDATA) = wrap { this / t }
  override fun div(t: XAttribute) = wrap {this / t }

  override fun <T : Any> div(t: XUnknownElement<T>) = wrap { this / t }
  override fun <T : Any> div(t: XSub<T>) = wrap { this / t }

  private fun <T : Any> wrap(builder : XMLBuilderImpl.() -> XBindProperty<T>) : XBindProperty<List<T>> =
          elementsBefore.wrap(XmlElementsWrap(elementAsList) { XMLBuilderImpl(elementsAfter).builder() })
}
