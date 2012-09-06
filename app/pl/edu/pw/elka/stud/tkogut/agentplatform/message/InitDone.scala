package pl.edu.pw.elka.stud.tkogut.agentplatform.message

import java.util
import pl.edu.pw.elka.stud.tkogut.agentplatform.Agent


/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut@hotmail.com
 * Date:  22.06.12
 * Time:  20:24
 *
 */

/**
 * Agent when done initialization is obliged to send this message to @see{AgentManagmentPlatform}
 * @param from Reference to this agent
 */
class InitDone(from: Agent) extends Message(from, util.UUID.randomUUID()) {

}
