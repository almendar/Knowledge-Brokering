package pl.edu.pw.elka.stud.tkogut.passim.search.citeseer

import pl.edu.pw.elka.stud.tkogut.brokering.SearchAgent
import pl.edu.pw.elka.stud.tkogut.brokering.message.QueryMessage
import pl.edu.pw.elka.stud.tkogut.passim.PassimDialect
import collection.mutable
import pl.edu.pw.elka.stud.tkogut.searchengine.citeseer._
import pl.edu.pw.elka.stud.tkogut.brokering.result.SingleResult
import pl.edu.pw.elka.stud.tkogut.brokering.dialect.Attribute

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  24.06.12
 * Time:  20:53
 *
 */

object CiteSeerSearcherAgent {
  final val NAME = "CiteSeer search agent"
  final val DESC = ""
}

class CiteSeerSearcherAgent extends SearchAgent(CiteSeerSearcherAgent.NAME, CiteSeerSearcherAgent.DESC) {

  val gate = new CiteSeerGate

  import PassimDialect._


  /**
   * Method invoked when received QueryMsg.
   * Should be reimplemented
   * @param queryMsg Message with query text
   * @return List of results
   */
  def search(queryMsg: QueryMessage): List[SingleResult] = {
    var returnResult: List[SingleResult] = null
    queryMsg.typeOfQuery match {
      case PERSON => gate.setAuthor(queryMsg.query)
      case PUBLICATION => gate.setTitle(queryMsg.query)
    }
    val searchResults: List[mutable.Map[String, String]] = gate.search(100)
    returnResult = searchResults.map {
      x =>
        val result = new SingleResult("CiteseerX")
        val mapOfAttributes = (INFOS zip capabilitiesList).toMap
        x.foreach {
          case (k, v) =>
            mapOfAttributes.getOrElse(k, null) match {
              case x: Attribute => result.add(x, v)
              case null => //nothing
            }
        }
        result
    }
    returnResult.toList
  }

  private val capabilitiesList =
    List(
      (personName),
      (publicationTitle),
      (publicationYear)
    )
  val capabilities = List(PERSON, PUBLICATION)
  private val INFOS = List(CiteSeerInfoExtraction.AUTHORS, CiteSeerInfoExtraction.TEXT, CiteSeerInfoExtraction.YEAR)
}
