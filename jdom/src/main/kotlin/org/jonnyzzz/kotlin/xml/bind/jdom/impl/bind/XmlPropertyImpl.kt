package org.jonnyzzz.kotlin.xml.bind.jdom.impl.bind

import org.jdom2.Element
import org.jonnyzzz.kotlin.xml.bind.XCallback
import org.jonnyzzz.kotlin.xml.bind.XProperty
import org.jonnyzzz.kotlin.xml.bind.XReadOnly
import kotlin.reflect.KProperty

/**
 * Created by eugene.petrenko@gmail.com
 */
internal  abstract class XPropertyBase<T : Any>(override var value: T?,
                                                override var saveOrder: Int? = null) : XBindProperty<T> {
  constructor(p:  XBindProperty<T>) : this(p.value, p.saveOrder) { }

  operator final override fun getValue(thisRef: Any, property: KProperty<*>): T? = value
  operator final override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
    this@XPropertyBase.value = value
  }

  override fun minus(defaultValue: T?): XProperty<T> {
    value = defaultValue
    return this
  }

  override fun minus(readOnly: XReadOnly): XProperty<T> = object : XPropertyDelegate<T>(this) {
    override fun save(scope: () -> Element) {
      //NOP
    }
  }

  override fun minus(event: XCallback<T>): XProperty<T> = object : XPropertyDelegate<T>(this) {
    override fun load(scope: Element?) {
      this@XPropertyBase.load(scope)
      value = event.onLoaded(value)
    }

    override fun save(scope: () -> Element) {
      value = event.onBeforeSave(value)
      this@XPropertyBase.save(scope)
    }
  }
}

internal abstract class XPropertyDelegate<T : Any>(val property: XBindProperty<T>)  : XPropertyBase<T>(property) {
  override fun load(scope: Element?) { property.load(scope) }
  override fun save(scope: () -> Element) { property.save(scope) }

  override var value: T?
    get() = property.value
    set(value) { property.value = value }

  override var saveOrder: Int?
    get() = property.saveOrder
    set(value) { property.saveOrder = saveOrder }
}

internal abstract class XPropertyImpl<T : Any>(override var value: T?,
                                               override var saveOrder: Int? = null) : XPropertyBase<T>(value, saveOrder) {
  final override fun save(scope: () -> Element) {
    val v = value
    if (v != null) saveImpl(v, scope())
  }

  override fun load(scope: Element?) {
    value = loadImpl(scope)
  }

  protected abstract fun saveImpl(value: T, scope: Element)
  protected abstract fun loadImpl(scope: Element?): T?
}
