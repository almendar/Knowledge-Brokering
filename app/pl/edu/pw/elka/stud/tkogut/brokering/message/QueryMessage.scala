package pl.edu.pw.elka.stud.tkogut.brokering.message

import java.util
import pl.edu.pw.elka.stud.tkogut.agentplatform.Agent
import pl.edu.pw.elka.stud.tkogut.brokering.dialect.Entity
import pl.edu.pw.elka.stud.tkogut.agentplatform.message.Message


/**
 * Class for sending queries
 * @param sender This agent.
 * @param token ID of the dialog.
 */
class QueryMessage(sender: Agent, token: util.UUID, val query: String, val typeOfQuery: Entity) extends Message(sender, token) {
  def getInfo: (Agent, util.UUID, String, Entity) = {
    (sender, token, query, typeOfQuery)
  }
}