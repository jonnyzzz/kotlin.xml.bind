package org.jonnyzzz.kotlin.xml.bind.jdom.impl

import kotlin.reflect.full.declaredMemberProperties


internal fun <T : Any> Class<T>.declaredPropertyNames(): Set<String> {
  return this.kotlin.declaredMemberProperties.map { x -> x.name }.toSortedSet()
}

internal fun <T : Any, Y : Any> T.delegatedProperties(propertyDelegate: Class<Y>): List<Y> = scan(propertyDelegate, this)

private fun <T : Any, Y : Any> scan(propertyDelegate: Class<Y>, obj: T, clazz: Class<T> = obj.javaClass): List<Y> {
  val names = clazz.declaredPropertyNames()

  val declaredFields = arrayListOf<Y>()
  for (it in clazz.declaredFields) {
    val name = it.name ?: continue
    if (!name.endsWith("\$delegate")) continue
    if (!names.contains(name.split('$').first())) continue
    it.type ?: continue
    it.isAccessible = true
    val value = it.get(obj)

    if (!propertyDelegate.isInstance(value)) continue
    declaredFields.add(propertyDelegate.cast(value)!!)
  }

  val sup = clazz.getSuperclass()
  if (sup != null && sup != Any::class.java) {
    return declaredFields + scan<Any, Y>(propertyDelegate, obj, sup as Class<Any>)
  } else {
    return declaredFields
  }
}
