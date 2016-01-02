package org.jonnyzzz.kotlin.xml.bind.jdom.impl.bind

import org.jdom2.Element

/**
 * Created by eugene.petrenko@gmail.com
 */
interface XmlBind {
  fun load(scope : Element?)
  fun save(scope : () -> Element)

  val saveOrder : Int?
}
