package org.jonnyzzz.kotlin.xml.bind.jdom.impl

tailrec fun <T : Annotation, R> Class<R>.getAnnotationRec(ax: Class<T>): T? {
  val root = this.getAnnotation(ax)
  if (root != null) return root
  val sup = this.superclass ?: return null
  return sup.getAnnotationRec(ax)
}

public inline fun <T> having(t: T, builder: T.() -> Unit): T {
  t.builder()
  return t
}
