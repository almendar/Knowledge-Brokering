package pl.edu.pw.elka.stud.tkogut.searchengine

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.Imports
import java.util.NoSuchElementException
import collection.mutable
import org.apache.commons.logging.Log


class MongoPublication {
  var title = ""
  var authors = List.empty[String]
  var publicationYear : Option[Int] = None
  var ISBN : Option[String] = None

  override def toString() : String = {
    val sb = new StringBuilder
    sb append title
    sb append " By "
    sb append authors.mkString(", ")
    if(publicationYear!=None) {
    sb append " from year "
    sb append publicationYear.get
  }
    if(ISBN!=None) {
    sb append " ISBN:"
    sb append ISBN.get
    }
    sb.toString()
  }

}

class MongoDblpGate {
  private var connectionPort = MongoDatabase.DEFAULTR_PORT
  private var connectionHost = MongoDatabase.DEFAULT_HOST
  private var mongoConn: MongoConnection = null
  private var passimDB: MongoDB = null
  private def documentsCollection = passimDB("documents")
  private def peopleCollection = passimDB("people")


  def connect(adress: String = MongoDatabase.DEFAULT_HOST, port: Int = MongoDatabase.DEFAULTR_PORT) = {
    connectionHost = adress
    connectionPort = port
    mongoConn = MongoConnection(connectionHost, connectionPort)
    passimDB = mongoConn("Dblp")
  }





  def searchPublication(keyWord:String, limitNr:Int=300) : List[MongoPublication] = {
    var builder = MongoDBObject.newBuilder
    builder += "titleAliases" -> (".*" + keyWord).r
    val query = builder.result()
    val it  = documentsCollection.find(query).limit(limitNr)
    val publicationBuffer = scala.collection.mutable.ArrayBuffer.empty[MongoPublication]
    it.foreach { document =>
      try {
        val publication = new MongoPublication
        publication.title = document.getAs[MongoDBList]("titleAliases").get.map(_.toString).head
        val authorsIDs: mutable.Seq[Imports.ObjectId] = document.getAs[MongoDBList]("authorIds").get.map(_.asInstanceOf[ObjectId])
        var builder = MongoDBObject.newBuilder
        val q = "_id" $in authorsIDs
        val nameBuffer = scala.collection.mutable.ArrayBuffer.empty[String]
        peopleCollection.find(q).limit(limitNr).foreach{singlePerson =>
          nameBuffer += singlePerson.getAs[MongoDBList]("nameAliases").get.map(_.toString).head

        }
        publication.authors = nameBuffer.toList
        publication.publicationYear = document.getAs[Int]("year")
        publication.ISBN = document.getAs[String]("isbn")
        publicationBuffer += publication
      }
      catch {
        case x:NoSuchElementException => println("Problem:" + x.getMessage)
      }

    }
    publicationBuffer.toList
  }
  def searchAuthor(keyWord:String, limitNr:Int=300)  : List[MongoPublication] = {
    var builder = MongoDBObject.newBuilder
    builder += "nameAliases" -> (".*" + keyWord).r
    val query = builder.result()
    val it  = peopleCollection.find(query).limit(limitNr)
    val publicationBuffer = scala.collection.mutable.ArrayBuffer.empty[MongoPublication]
    it.foreach { person =>
      try {
        //val authorName = person.getAs[MongoDBList]("nameAliases").get.map(_.toString).tail
        val authorID = person.getAs[ObjectId]("_id").get
        val q = "authorIds" $in Seq(authorID)
        documentsCollection.find(q).limit(limitNr).foreach { document =>
          val publication = new MongoPublication
          publication.title = document.getAs[MongoDBList]("titleAliases").get.map(_.toString).head
          val authorsIDs = document.getAs[MongoDBList]("authorIds").get.map(_.asInstanceOf[ObjectId])
          var builder = MongoDBObject.newBuilder
          val q = "_id" $in authorsIDs
          val nameBuffer = scala.collection.mutable.ArrayBuffer.empty[String]
          peopleCollection.find(q).limit(limitNr).foreach{singlePerson =>
            nameBuffer += singlePerson.getAs[MongoDBList]("nameAliases").get.map(_.toString).head
          }
          publication.authors = nameBuffer.toList
          publication.publicationYear = document.getAs[Int]("year")
          publication.ISBN = document.getAs[String]("isbn")
          publicationBuffer+=publication
        }
      }
      catch {
        case x:NoSuchElementException => //Silently skip
      }
    }
    publicationBuffer.toList
  }

  def disconnect() = {
    mongoConn.close()
  }
}

object MongoDatabase {
  final val DEFAULTR_PORT = 27017
  final val DEFAULT_HOST = "localhost"
}