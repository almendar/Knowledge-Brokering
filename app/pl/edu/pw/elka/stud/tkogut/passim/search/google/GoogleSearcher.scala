package pl.edu.pw.elka.stud.tkogut.passim.search.google

import java.io.IOException
import pl.edu.pw.elka.stud.tkogut.brokering.message.QueryMessage
import pl.edu.pw.elka.stud.tkogut.brokering.SearchAgent
import pl.edu.pw.elka.stud.tkogut.passim.PassimDialect
import scala.collection.mutable.ArrayBuffer
import pl.edu.pw.elka.stud.tkogut.searchengine.GoogleSearch
import pl.edu.pw.elka.stud.tkogut.brokering.result.SingleResult


object GoogleSearcher {
  val NAME = "Google Search Agent"
  val DESC = "Provides access to Google search enginee"
}

class GoogleSearcher extends SearchAgent(GoogleSearcher.NAME, GoogleSearcher.DESC) {

  private val API_KEY = "AIzaSyCZ72XdhT4SOG2BUdGFA043jNxT9Fd4wPk"
  val googleGate = new GoogleSearch(API_KEY)

  override final val capabilities = List(PassimDialect.WEBSITE)

  private def processQuery(queryMsg: QueryMessage): List[SingleResult] = {
    val results = ArrayBuffer.empty[SingleResult]
    try {
      val googleResults = googleGate.search(queryMsg.query)
      googleResults.foreach { (r) =>
        import PassimDialect._
        val singleResult = new SingleResult("Google.com")
        singleResult.add(websiteUrl, r.url.toString)
        singleResult.add(websiteTitle, r.title)
        singleResult.add(websiteBrief, r.description)
        results += singleResult
      }
    } catch {
      case eio: IOException =>
        speak("Google did not provide search results:" + eio.toString)
    }
    return if (results.nonEmpty) results.toList else Nil
  }

  def search(queryMsg: QueryMessage): List[SingleResult] = {
    queryMsg match {
      case x: QueryMessage =>
        speak("Searching for %s for agent %s".format(x.query, x.from.getName))
        processQuery(x)
      case _ => null
    }
  }
}