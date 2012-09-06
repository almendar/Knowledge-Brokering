package pl.edu.pw.elka.stud.tkogut.agentplatform.message

import pl.edu.pw.elka.stud.tkogut.agentplatform.Agent
import java.util.UUID

/**
 * Created with IntelliJ IDEA.
 * User: tomek
 * Date: 20.06.12
 * Time: 17:34
 * To change this template use File | Settings | File Templates.
 */


object Message {
  final def generateToken = UUID.randomUUID()
}

class Message(val from: Agent, val token: UUID)
