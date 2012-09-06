package pl.edu.pw.elka.stud.tkogut.passim.search.bing

import scala.collection.mutable.ArrayBuffer
import pl.edu.pw.elka.stud.tkogut.brokering.SearchAgent
import pl.edu.pw.elka.stud.tkogut.brokering.message.QueryMessage
import pl.edu.pw.elka.stud.tkogut.passim.PassimDialect
import pl.edu.pw.elka.stud.tkogut.searchengine.BingSearch
import pl.edu.pw.elka.stud.tkogut.brokering.result.SingleResult


/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  24.06.12
 * Time:  19:37
 *
 */

object BingSearcherAgent {
  final val NAME = "Bing Search Agent"
  final val DESC = "Bing searching agent"
}

class BingSearcherAgent extends SearchAgent(BingSearcherAgent.NAME, BingSearcherAgent.DESC) {

  override final val capabilities = List(PassimDialect.WEBSITE)
  val bingGate = new BingSearch("8A4C8362BAF8F435BCF3F8854CBEF493006E398A")

  private def processQuery(queryMsg: QueryMessage): List[SingleResult] = {
    val results = ArrayBuffer.empty[SingleResult]
    val bingResults = bingGate.search(queryMsg.query)
    bingResults.foreach {
      (r) =>
        import PassimDialect._
        val singleResult = new SingleResult("Bing.com")
        singleResult.add(websiteUrl, r.url.toString)
        singleResult.add(websiteTitle, r.title)
        singleResult.add(websiteBrief, r.description)
        singleResult.add(websiteDate, r.date.toString)
        results += singleResult
    }
    return if (results.nonEmpty) results.toList else Nil
  }

  def search(queryMsg: QueryMessage): List[SingleResult] = {
    speak("Searching for %s for agent %s".format(queryMsg.query, queryMsg.from.getName))
    processQuery(queryMsg)

  }
}
