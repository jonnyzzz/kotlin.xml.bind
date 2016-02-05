package org.jonnyzzz.kotlin.xml.bind

import kotlin.properties.ReadWriteProperty

object XName ///element name
object XText ///element text
object XCDATA  ///element text. Written as CDATA
open class XUnknownElement<T>(val clazz : Class<T>) ///stores all un-matched XML part as an jdom Element
data class XAttribute(val name : String) ///named attribute
data class XSub<T>(val clazz : Class<T>) ///bind a sub-class

object XAnyElement  ///matches to any element
object XAnyElements ///matches to a list of any elements

data class XElements(val name : String) ///matches to a list of elements of given name

object XReadOnly

data class XCallback<T : Any>(val onLoaded : (T?) -> T? = {i->i}, val onBeforeSave : (T?) -> T? = {i->i})


interface XProperty<T : Any> : ReadWriteProperty<Any, T?> {
  operator fun minus(defaultValue : T?) : XProperty<T>
  operator fun minus(readOnly : XReadOnly) : XProperty<T>
  operator fun minus(event : XCallback<T>) : XProperty<T>
}

interface XMLsBuilder {
  operator fun div(element : String) : XMLsBuilder

  operator fun div(t : XName) : XProperty<List<String>>
  operator fun div(t : XText) : XProperty<List<String>>
  operator fun div(t : XCDATA) : XProperty<List<String>>
  operator fun div(t : XAttribute) : XProperty<List<String>>

  operator fun <T : Any> div(t : XUnknownElement<T>) : XProperty<List<T>>
  operator fun <T : Any> div(t : XSub<T>) : XProperty<List<T>>
}

interface XMLBuilder {
  operator fun div(element : XElements) : XMLsBuilder
  operator fun div(element : XAnyElements) : XMLsAnyBinder

  operator fun div(element : String) : XMLBuilder
  operator fun div(element : XAnyElement) : XMLAnyBinder

  operator fun div(t : XName) : XProperty<String>
  operator fun div(t : XText) : XProperty<String>
  operator fun div(t : XCDATA) : XProperty<String>
  operator fun div(t : XAttribute) : XProperty<String>

  operator fun <T : Any> div(t : XUnknownElement<T>) : XProperty<T>
  operator fun <T : Any> div(t : XSub<T>) : XProperty<T>
}

interface XMLAnyBinder {
  operator fun <T : Any> div(t : XSub<T>) : XProperty<T>
  operator fun <T : Any> div(t : XUnknownElement<T>) : XProperty<T>
}

interface XMLsAnyBinder {
  operator fun <T : Any> div(t : XSub<T>) : XProperty<List<T>>
  operator fun <T : Any> div(t : XUnknownElement<T>) : XProperty<List<T>>
}

@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class XRoot(val name : String)

interface XMLRootBuilder : XMLBuilder {
  fun saveOrder(order : Int) : XMLBuilder
  operator fun get(order : Int) : XMLBuilder = saveOrder(order)
}
