package pl.edu.pw.elka.stud.tkogut.passim.search.dblp

import java.util
import scala.collection.JavaConversions._
import com.mongodb.casbah.Imports
import collection.mutable.ArrayBuffer
import pl.edu.pw.elka.stud.tkogut.brokering.SearchAgent
import pl.edu.pw.elka.stud.tkogut.brokering.message.QueryMessage
import pl.edu.pw.elka.stud.tkogut.passim.PassimDialect
import pl.edu.pw.elka.stud.tkogut.searchengine.{MongoPublication, MongoDblpGate}
import pl.edu.pw.elka.stud.tkogut.brokering.result.SingleResult

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  02.07.12
 * Time:  18:17
 *
 */

object MongoDblpSearcher {
  val NAME = "MongoDblpAgent"
  val DESC = "Search agent to DBLP stored in a mongodb database"
}

class MongoDblpSearcher extends SearchAgent(MongoDblpSearcher.NAME, MongoDblpSearcher.DESC) {

  import PassimDialect._

  override final val capabilities = List(PassimDialect.PERSON, PassimDialect.PUBLICATION)

  val dblpGate = new MongoDblpGate()
  dblpGate.connect()

  def search(queryMsg: QueryMessage) = {
    var returnResult = scala.collection.mutable.HashMap.empty[String,SingleResult]
    val (sender, token, query, typeOfQuery) = queryMsg.getInfo

    val searchResults: List[MongoPublication] = typeOfQuery match {
      case PERSON => dblpGate.searchAuthor(query)
      case PUBLICATION => dblpGate.searchPublication(query)
    }

   for (i <- searchResults) {
     if(!returnResult.contains(i.title)) {
       val singleRes = new SingleResult("DBLP")
       singleRes.add(personName, i.authors.mkString(","))
       singleRes.add(publicationTitle, i.title)
       i.publicationYear match {
         case Some(year) => singleRes.add(publicationYear, year.toString)
         case _ =>
       }
       i.ISBN match {
         case Some(isbn) => singleRes.add(publicationISBN, isbn)
         case _ =>
       }
        returnResult += (i.title->singleRes)
     }
    }
    returnResult.values.toList
  }
}