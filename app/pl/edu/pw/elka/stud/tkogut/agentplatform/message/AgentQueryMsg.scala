package pl.edu.pw.elka.stud.tkogut.agentplatform.message

import java.util
import pl.edu.pw.elka.stud.tkogut.agentplatform.Agent

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut@hotmail.com
 * Date:  20.06.12
 * Time:  22:46
 *
 */


/**
 * This message is sent to @see{YellowPagesAgent}. In response agent should get a @see{AgentListQueryMessage}
 * with agent list
 * @param from Source agent
 * @param constraintFunc  Function takes an agent and says if caller is interested in this agent.
 */
class AgentQueryMsg(from: Agent, constraintFunc: (Agent) => Boolean) extends Message(from, util.UUID.randomUUID()) {
  def apply(a: Agent) = constraintFunc(a)
}