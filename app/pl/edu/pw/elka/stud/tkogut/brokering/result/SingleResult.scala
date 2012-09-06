package pl.edu.pw.elka.stud.tkogut.brokering.result

import scala.collection._
import pl.edu.pw.elka.stud.tkogut.brokering.dialect._

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  01.07.12
 * Time:  15:25
 *
 */

class SingleResult(val source:String) 
{
  val _values =  mutable.Map.empty[Attribute,String]
  val _valuesSeq = mutable.Map.empty[Attribute,Seq[String]]
  def add(attr:Attribute,value:String) = _values += (attr->value)
  def addSeq(attribute:Attribute,values:Seq[String]) {
    _valuesSeq += (attribute->values)
  }
  def getValue(attr:Attribute) = Option(_values.getOrElse(attr,null))
  def getEntriesNumber = _values.keys.size
  
  override def toString() : String =
  {
    val sb = new StringBuilder
    sb append "From:"
    sb append source
    sb append "@"
    for((x,y) <- _values) {
      sb append x
      sb append "-"
      sb append y
      sb append ","
    }
    sb.toString()
  }
}
