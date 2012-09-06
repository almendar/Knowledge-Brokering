package pl.edu.pw.elka.stud.tkogut.agentplatform.yellowpages

import pl.edu.pw.elka.stud.tkogut.agentplatform.message.Message

import java.util

class AgentRegisteredACK(token: util.UUID) extends Message(YellowPages, token)
