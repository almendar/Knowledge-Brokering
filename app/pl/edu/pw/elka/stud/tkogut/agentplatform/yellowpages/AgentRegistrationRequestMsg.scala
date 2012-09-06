package pl.edu.pw.elka.stud.tkogut.passim.agents.yellowpages

import java.util
import pl.edu.pw.elka.stud.tkogut.agentplatform.message.Message
import pl.edu.pw.elka.stud.tkogut.agentplatform.Agent


/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut@hotmail.com
 * Date:  20.06.12
 * Time:  18:11
 *
 */

case class AgentRegistrationRequestMsg(agentToRegister: Agent) extends Message(agentToRegister, util.UUID.randomUUID())


