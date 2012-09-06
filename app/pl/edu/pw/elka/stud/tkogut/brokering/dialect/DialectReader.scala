package pl.edu.pw.elka.stud.tkogut.brokering.dialect

import scala.xml.{ XML, Elem }
import scala.collection.mutable.ArrayBuffer

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  15.07.12
 * Time:  13:12
 *
 */

class DialectReader {

  private[dialect] var root: scala.xml.Elem = null
  private[dialect] var entitesSeq: scala.xml.NodeSeq = null
  private[dialect] var mappings: scala.xml.NodeSeq = null
  private[dialect] var mappins = ArrayBuffer.empty[List[(Entity,Attribute)]]
  private[dialect] var entites = ArrayBuffer.empty[Entity]
  private[dialect] var entitesMap = scala.collection.mutable.Map.empty[String,Entity]
  private[dialect] var attributesMap = scala.collection.mutable.Map.empty[String,Attribute]
  private[dialect] var dialectName: String = null

  private var checkMappings = false

  def load(fileName: String) {
    read(fileName)
    loadDialectName()
    loadEntites()
    loadMappings()
  }
  
  
  def getDialect() : Dialect = {
    val dialect = new Dialect(dialectName)
    dialect.addEntites(entites)
    dialect.mappings = mappins.toList
    return dialect
  }

  private[dialect] def loadDialectName() {
    dialectName = (root \ "@name").text
  }
  private[dialect] def read(fileName: String) {
    root = XML.loadFile(fileName)
    loadDialectName()
  }

  private[dialect] def setCheckingMappins(): DialectReader = {
    checkMappings = true
    return this
  }

  private[dialect] def loadEntites() {
    entitesSeq = root \\ "entites"
    for (i <- entitesSeq \\ "entity") {
      val name = (i \ "@name").text
      val e = new Entity(name)
      entitesMap += (name->e)
      
      val attributesList = i \ "attribute"
      for (j <- attributesList) {
        val attributeNameText = (j \ "@name").text
        val attributeTypeText = (j \ "@type").text
        val isMultivalText = (j \ "@multival").text
        val attributeType = attributeTypeText match {
          case "name" => AttributeType.NAME
          case "adress" => AttributeType.ADDRESS
          case "grad" => AttributeType.GRAD
          case "phone_number" => AttributeType.PHONE_NUMBER
          case "url" => AttributeType.URL
          case "email" => AttributeType.EMAIL
          case "date_time" => AttributeType.DATE_TIME
          case "integer" => AttributeType.INTEGER
          case "float" => AttributeType.FLOAT
          case "string" => AttributeType.STRING
        }
        val multival = isMultivalText match {
          case "true" => true
          case "false" => false
          case "" => false
        }
        val attribute = Attribute(attributeNameText, attributeType, multival)
        e.addAttributes(attribute)
        attributesMap+=(attributeNameText -> attribute)
      }
      entites += e
    }
  }

  private[dialect] def loadMappings() {
    mappings = root \\ "mappings"
    for (i <- mappings \\ "map")  {
      val qs = i \\ "Q"
      val map = ArrayBuffer.empty[(Entity,Attribute)]
      for (j <- qs) yield {
        val entityName = (j \\ "@entity").text
        val attributeName = (j \\ "@attribute").text
        if (checkMappings)
          assertMappings(entityName, attributeName)
        map += ( (entitesMap(entityName) , attributesMap(attributeName)) )
      }
      mappins += map.toList
    }
  }

  private def assertMappings(entityName: String, attributeName: String): Unit = {
    /*** Check if is appropriate */
    val entityOption = entites.find(_.name == entityName)
    entityOption match {
      case Some(x) => assert(x.attributes.exists(_.name == attributeName))
      case None => assert(false)
    }
    /**********************************/
  }

}