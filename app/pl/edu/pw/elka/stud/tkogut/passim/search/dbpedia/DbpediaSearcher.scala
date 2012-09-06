package pl.edu.pw.elka.stud.tkogut.passim.search.dbpedia

import pl.edu.pw.elka.stud.tkogut.brokering.SearchAgent
import pl.edu.pw.elka.stud.tkogut.passim.PassimDialect
import pl.edu.pw.elka.stud.tkogut.brokering.message.QueryMessage
import collection.mutable.ArrayBuffer
import pl.edu.pw.elka.stud.tkogut.brokering.result.SingleResult
import pl.edu.pw.elka.stud.tkogut.searchengine.DbpediaUniversitySearch
import collection.immutable.HashMap
import collection.parallel.mutable

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  04.08.12
 * Time:  22:59
 *
 */

object DbpediaSearcher {
  final val NAME = "DBPedia searcher"
  final val DESC = "Looks for universities in Dbpedia"
}

class DbpediaSearcher extends SearchAgent(DbpediaSearcher.NAME, DbpediaSearcher.DESC) {
  override final val capabilities = List(PassimDialect.UNIVERSITY )
  val gate = new DbpediaUniversitySearch()

  def search(queryMsg: QueryMessage) = {
    var returnResult = scala.collection.mutable.HashMap.empty[String,SingleResult]

    val returns = gate.search(queryMsg.query)


    for (i<-returns) {
      val sinRes = if(returnResult.contains(i.name)) returnResult(i.name) else new SingleResult("Dbpedia")
      sinRes.add(PassimDialect.universityName, i.name)
      sinRes.add(PassimDialect.universityHomeCity,i.city)
      sinRes.add(PassimDialect.universityFoundationYear,i.yearEstablished.toString)
      sinRes.add(PassimDialect.universityCountry,i.country)
      sinRes.add(PassimDialect.universityHomepage,i.homepage)
      returnResult+=(i.name->sinRes)
    }
    returnResult.values.toList
  }
}
