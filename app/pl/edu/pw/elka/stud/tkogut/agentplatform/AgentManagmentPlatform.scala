package pl.edu.pw.elka.stud.tkogut.agentplatform

import pl.edu.pw.elka.stud.tkogut.agentplatform.yellowpages.YellowPages
import pl.edu.pw.elka.stud.tkogut.agentplatform.message.InitDone




/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut@hotmail.com
 * Date:  20.06.12
 * Time:  18:11
 *
 */

object AgentManagmentPlatform extends Agent("", "") {
  var preloadedAgents: List[Agent] = null
  def startPlatform(preloadAgents : List[Agent]) {
    this.preloadedAgents = preloadAgents
    AgentManagmentPlatform.start()
    YellowPages.start()
  }

  override def act() = {
    receive {
      case x: InitDone =>
        if (x.from == YellowPages)
          preloadedAgents.foreach(_.start())
    }
  }
}
