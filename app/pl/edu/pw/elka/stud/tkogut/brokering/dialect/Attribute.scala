package pl.edu.pw.elka.stud.tkogut.brokering.dialect
import collection.mutable.ListBuffer

import AttributeType._

case class Attribute(name: String, attrType: AttributeType, multiValue:Boolean = false) {

  private val parentEntities = new ListBuffer[Entity]


  def registerEntity(entity:Entity) {
    parentEntities.append(entity)
    
  }
  override def toString = name + ":" + attrType

}




