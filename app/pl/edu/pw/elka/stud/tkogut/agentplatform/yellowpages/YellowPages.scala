package pl.edu.pw.elka.stud.tkogut.agentplatform.yellowpages

import pl.edu.pw.elka.stud.tkogut.agentplatform.{Agent,AgentManagmentPlatform}
import pl.edu.pw.elka.stud.tkogut.agentplatform.message.{AgentListMsg, InitDone,AgentQueryMsg}
import pl.edu.pw.elka.stud.tkogut.passim.agents.yellowpages.AgentRegistrationRequestMsg

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut@hotmail.com
 * Date:  20.06.12
 * Time:  17:44
 *
 */

object YellowPages extends Agent(YellowPages.NAME, YellowPages.DESC) {
  final val NAME = "Yellow Pages"
  final val DESC = "Directory of currenlty registered agents"
  val agentsBook = scala.collection.mutable.HashSet.empty[Agent];

  override def act() = {
    AgentManagmentPlatform ! new InitDone(this)
    speak("Started to act")
    loop {
      receive {
        case msg: AgentRegistrationRequestMsg =>
          if(!agentsBook.contains(msg.from)) {
            agentsBook += msg.agentToRegister
            speak("Registered agent:" + msg.agentToRegister)
          }
            val respond = new AgentRegisteredACK(msg.token)
            sendMessage(msg.agentToRegister, respond)

        case msg: AgentQueryMsg =>
          val resultList = agentsBook.filter(msg(_)).toList;
          val responsMsg = new AgentListMsg(msg.token, resultList)
          sendMessage(msg.from, responsMsg);
      }
    }
  }
}
