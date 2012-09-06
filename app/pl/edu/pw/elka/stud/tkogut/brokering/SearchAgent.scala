package pl.edu.pw.elka.stud.tkogut.brokering


import pl.edu.pw.elka.stud.tkogut.agentplatform.Agent
import pl.edu.pw.elka.stud.tkogut.brokering.dialect.Entity
import pl.edu.pw.elka.stud.tkogut.brokering.message.{SearchResultMessage,QueryMessage}
import pl.edu.pw.elka.stud.tkogut.brokering.result.SingleResult


/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut@hotmail.com
 * Date:  20.06.12
 * Time:  22:31
 *
 */

abstract class SearchAgent(name: String, description: String) extends Agent(name, description) {

  override def act() = {
    while (!isRegistered) {
      registerInYellowPages()
    }
    loop {
      receive {
        case tqm: QueryMessage =>
          val results: List[SingleResult] = search(tqm)
          val resMsg = new SearchResultMessage(this, tqm.token)
          resMsg.addResults(results)
          sendMessage(tqm.from, resMsg)
      }
    }
  }

  /**
   * Method invoked when received QueryMsg.
   * Should be reimplemented
   * @param queryMsg Message with query text
   * @return List of results
   */
  def search(queryMsg: QueryMessage): List[SingleResult]

  //@TODO Think of using Future object

  val capabilities: List[Entity]
}
