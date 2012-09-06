package pl.edu.pw.elka.stud.tkogut.passim.search.arnetminer

import pl.edu.pw.elka.stud.tkogut.brokering.SearchAgent
import pl.edu.pw.elka.stud.tkogut.passim.PassimDialect
import pl.edu.pw.elka.stud.tkogut.brokering.message.QueryMessage
import pl.edu.pw.elka.stud.tkogut.searchengine.ArnetMinerGate
import pl.edu.pw.elka.stud.tkogut.brokering.result.SingleResult
import pl.edu.pw.elka.stud.tkogut.brokering.dialect.Attribute

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  08.07.12
 * Time:  15:49
 *
 */

object ArnetMinerSearcher {
  val NAME = "ArnetMinerSearcher"
  val DESC = "Arnetminer RESTful Service"
}

class ArnetMinerSearcher extends SearchAgent(ArnetMinerSearcher.NAME, ArnetMinerSearcher.DESC) {


  override final val capabilities = List(PassimDialect.PERSON,PassimDialect.PUBLICATION)

  //  publicationTitle->ArnetMinerGate.TITLE,
  //  publicationYear->ArnetMinerGate.PUBYEAR


  /**
  Capability(personName,true),
   * Method invoked when received QueryMsg.
   * Should be reimplemented
   * @param queryMsg Message with query text
   * @return List of results
   */
  def search(queryMsg: QueryMessage): List[SingleResult] = {
    var returnResult: List[SingleResult] = null
    var results: List[Map[String, String]] = null
    val query = queryMsg.query
    val queryType = queryMsg.typeOfQuery
    gate.query = query
    gate.resultsLimit = 100

    try {
      queryType match {
        case PassimDialect.PERSON =>
          results = gate.searchExpert()
        case PassimDialect.PUBLICATION =>
          results = gate.searchPublication()
      }
      returnResult = results.map {
        (x) =>
          val singleResult = new SingleResult("Arnetminer.com")
          x.foreach {
            case (k: String, v: String) =>
              mappingArnetminerToAttribute.getOrElse(k, null) match {
                case x: Attribute => singleResult.add(x, v)
                case null => //do nothing
              }
          }
          singleResult
      }
    }
    catch {
      case x: java.io.IOException =>
        speak("There are problems with Arnetminer gate. %s".format(x.getLocalizedMessage))
        returnResult = Nil
    }
    returnResult.toList
  }


  import PassimDialect._

  private val mappingAttributeToArnetMiner = Map(
    personName -> ArnetMinerGate.NAME,
    personEmail -> ArnetMinerGate.EMAIL,
    personPhoneNumber -> ArnetMinerGate.PHONE,
    personHIndex -> ArnetMinerGate.HINDEX,
    personHomepage -> ArnetMinerGate.HOMEPAGE

  )

  private val mappingArnetminerToAttribute = Map(
    ArnetMinerGate.NAME -> personName,
    ArnetMinerGate.EMAIL -> personEmail,
    ArnetMinerGate.PHONE -> personPhoneNumber,
    ArnetMinerGate.HINDEX -> personHIndex,
    ArnetMinerGate.CITATIONNUM -> personCitationNum,
    ArnetMinerGate.HOMEPAGE -> personHomepage,
    ArnetMinerGate.PICTUREURL -> personPicture,
    ArnetMinerGate.TITLE -> publicationTitle,
    ArnetMinerGate.AUTHORS -> publicationAuthor,
    ArnetMinerGate.CITEDBY -> publicationCitationsNumber
  )
  val gate = new ArnetMinerGate
}
