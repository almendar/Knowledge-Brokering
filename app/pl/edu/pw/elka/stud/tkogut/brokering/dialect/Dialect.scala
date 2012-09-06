package pl.edu.pw.elka.stud.tkogut.brokering.dialect
import scala.collection.mutable.ArrayBuffer


class Dialect(dialectName: String)  {
  val entities =  scala.collection.mutable.Map.empty[String,Entity] 
 
  var mappings : List[List[(Entity,Attribute)]] = null
 
 
  def addEntity(entity:Entity) {
    this.entities += (entity.name -> entity)
  }
  
  def addEntites(entities:Iterable[Entity]) {
    this.entities ++= entities.map{(x) => x.name -> x}.toMap
  }
  
  
  def getEntityByName(name:String) : Entity = {
    return entities(name)
  }
  
  override def toString = {
    val sb = new StringBuilder
    sb ++= dialectName + "\n{\n"
    for (i <- entities) {
      sb ++= "\t" + i + "\n"
    }
    sb ++= "\n}"
    sb.mkString
  }
}







