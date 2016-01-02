package org.jonnyzzz.kotlin.xml.bind.jdom.impl.bind

import org.jdom2.Element
import kotlin.reflect.KProperty

/**
 * Created by eugene.petrenko@gmail.com
 */
abstract class XPropertyImpl<T : Any>(public override var value: T?,
                                      public override var saveOrder: Int? = null) : XBindPropertyDefs<T> {
  operator public final override fun getValue(thisRef: Any, property: KProperty<*>): T? = value
  operator public final override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
    this@XPropertyImpl.value = value
  }

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
