package org.jonnyzzz.kotlin.xml.bind.jdom.impl.bind

import org.jdom2.Element
import org.jonnyzzz.kotlin.xml.bind.XCallback
import org.jonnyzzz.kotlin.xml.bind.XProperty
import org.jonnyzzz.kotlin.xml.bind.XReadOnly

/**
 * Created by eugene.petrenko@gmail.com
 */
interface XBindProperty<T : Any> : XProperty<T>, XmlBind {
  var value: T?
  override var saveOrder: Int?
}

//default implementations now has higher priority in `by` syntax,
//so extracted default implementations to additional interface to avoid side effects
interface XBindPropertyDefs<T : Any> : XBindProperty<T> {
  override fun minus(defaultValue: T?): XProperty<T> {
    value = defaultValue
    return this
  }

  override fun minus(readOnly: XReadOnly): XProperty<T> = object : XBindProperty<T> by this {
    override fun save(scope: () -> Element) {
      //NOP
    }
  }

  override fun minus(event: XCallback<T>): XProperty<T> = object : XBindProperty<T> by this {
    override fun load(scope: Element?) {
      this@XBindPropertyDefs.load(scope)
      value = event.onLoaded(value)
    }

    override fun save(scope: () -> Element) {
      value = event.onBeforeSave(value)
      this@XBindPropertyDefs.save(scope)
    }
  }
}
