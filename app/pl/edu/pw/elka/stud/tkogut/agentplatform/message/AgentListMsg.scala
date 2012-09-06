package pl.edu.pw.elka.stud.tkogut.agentplatform.message

import java.util
import pl.edu.pw.elka.stud.tkogut.agentplatform.Agent
import pl.edu.pw.elka.stud.tkogut.agentplatform.yellowpages.YellowPages

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut@hotmail.com
 * Date:  20.06.12
 * Time:  22:50
 *
 */

/**
 * Message send as a response to AgentQueryMsg with list of agents.
 * @param token Identyfies convesation.
 * @param agentList List of agents that have meet the given constraints.
 */
class AgentListMsg(token: util.UUID, agentList: List[Agent]) extends Message(YellowPages, token) {
  def getAgents: List[Agent] = agentList
}
