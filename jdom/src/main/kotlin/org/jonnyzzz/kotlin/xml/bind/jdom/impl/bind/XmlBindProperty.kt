package org.jonnyzzz.kotlin.xml.bind.jdom.impl.bind

import org.jonnyzzz.kotlin.xml.bind.XProperty

/**
 * Created by eugene.petrenko@gmail.com
 */
interface XBindProperty<T : Any> : XProperty<T>, XmlBind {
  var value: T?
  override var saveOrder: Int?
}
