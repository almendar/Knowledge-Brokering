package pl.edu.pw.elka.stud.tkogut.brokering.message

import java.util
import pl.edu.pw.elka.stud.tkogut.agentplatform.message.Message
import pl.edu.pw.elka.stud.tkogut.agentplatform.{Agent}
import collection.mutable.ArrayBuffer
import pl.edu.pw.elka.stud.tkogut.brokering.result.{ResultDescription, SingleResult}

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  23.06.12
 * Time:  12:49
 *
 */

/**
 * Message send from search agents with results
 * @param from Search agent
 * @param token Token of the message
 */
class SearchResultMessage(from: Agent, token: util.UUID) extends Message(from, token) {
  private val results = ArrayBuffer.empty[SingleResult]

  def addResults(results: Iterable[SingleResult]) {
    this.results.appendAll(results)
  }

  def getResults = new ResultDescription(from,results.toList)

}
