package org.jonnyzzz.kotlin.xml.bind.jdom


import org.jonnyzzz.kotlin.xml.bind.jdom.impl.declaredPropertyNames
import org.jonnyzzz.kotlin.xml.bind.jdom.impl.delegatedProperties
import org.junit.Assert
import org.junit.Test
import java.util.*
import kotlin.properties.Delegates
import kotlin.reflect.KProperty


  public class XPropertiesTest {

  @Test
  public fun should_declaredPropertiesNames() {
    val names = (object {
      val X: String by Delegates.notNull()
      val Y by lazy(LazyThreadSafetyMode.NONE) { 42 }
    }).javaClass.declaredPropertyNames()

    Assert.assertEquals(TreeSet(listOf("X", "Y")), TreeSet(names))
  }

  @Test
  public fun should_declaredProperties() {
    val ps = (object {
      val X: String by Delegates.notNull()
      val Y by lazy(LazyThreadSafetyMode.NONE) { 42 }
    }).delegatedProperties(Any::class.java)

    Assert.assertEquals(ps.size, 2)
  }

  open class TestBase {
    private val XBase: String by Delegates.notNull()
    protected val YBase: String by Delegates.notNull()
    public val ZBase: String by Delegates.notNull()
  }

  open class TestA : TestBase()

  open class TestB : TestA() {
    private val X: String by Delegates.notNull()
    protected val Y: String by Delegates.notNull()
    public val Z: String by Delegates.notNull()
  }

  @Test
  public fun should_declaredPropertiesNames_inherit() {
    val ps = (object : TestB() {
      val A: String by Delegates.notNull()
    }).javaClass.declaredPropertyNames()

    Assert.assertEquals(TreeSet(listOf("A")), TreeSet(ps))
  }

  @Test
  public fun should_declaredProperties_inherit() {
    val ps = (object : TestB() {
      val A: String by Delegates.notNull()
    }).delegatedProperties(Any::class.java)

    Assert.assertEquals(ps.size, 7)
  }

  interface MMM
  interface QQQ
  open class TXProperty<T>(var value : T) : MMM {
    operator fun getValue(ref : Any?, info : KProperty<*>) : T = value
    operator fun setValue(ref : Any?, info : KProperty<*>, t : T) {
      value = t
    }
  }

  @Test
  public fun should_declaredProperties_filter() {
    val ps = (object : TestB() {
      val A: String by TXProperty("42")
    }).delegatedProperties(MMM::class.java)

    Assert.assertEquals(ps.size, 1)
  }

  @Test
  public fun should_declaredProperties_runtime_filter() {
    val ps = (object : TestB() {
      val TXProperty : TXProperty<String> = object : TXProperty<String>("42"), QQQ {}
      val A: String by TXProperty
    }).delegatedProperties(QQQ::class.java)

    Assert.assertEquals(ps.size, 1)
  }
}