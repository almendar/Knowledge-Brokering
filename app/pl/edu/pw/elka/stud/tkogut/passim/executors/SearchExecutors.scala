package pl.edu.pw.elka.stud.tkogut.passim.executors

import pl.edu.pw.elka.stud.tkogut.brokering.task.{QueryTask, Task, Executor}
import pl.edu.pw.elka.stud.tkogut.brokering.message.{InformationMessage, SearchResultMessage, QueryMessage}
import pl.edu.pw.elka.stud.tkogut.brokering.result.{InformationSnip, ResultDescription, SingleResult}
import pl.edu.pw.elka.stud.tkogut.brokering.{Broker, SearchAgent}
import collection.mutable
import pl.edu.pw.elka.stud.tkogut.agentplatform.Agent
import pl.edu.pw.elka.stud.tkogut.agentplatform.message.{AgentListMsg, AgentQueryMsg}
import pl.edu.pw.elka.stud.tkogut.agentplatform.yellowpages.YellowPages
import actors.TIMEOUT
import collection.mutable.ArrayBuffer
import pl.edu.pw.elka.stud.tkogut.passim.PassimDialect
import pl.edu.pw.elka.stud.tkogut.brokering.dialect.Attribute

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  17.07.12
 * Time:  20:50
 *
 */


abstract class SearchTaskExecutor(task: QueryTask, broker:Broker) extends Executor(task) {
  override def onCancel()   {}

  val searchAgents = mutable.HashSet.empty[SearchAgent]
  val resultsGather = ArrayBuffer.empty[ResultDescription]


  override val reaction: PartialFunction[Any, Unit] = super.reaction.orElse {
    case resultMsg: SearchResultMessage =>
      val token = resultMsg.token
      val from = resultMsg.from
      val resDesc : ResultDescription = resultMsg.getResults
      resultsGather += resDesc
      speak("Got %d results from %s".format(resDesc.r.size, from))
  }

  /**
   * Send message to YellowPage querying for SearchAgents.
   * Method will not return asynchronously but will wait for response.
   */
  def getSearchAgents() {
    speak("Trying to get search agents")
    def constraintFunc(a: Agent): Boolean = {
      a.isInstanceOf[SearchAgent] &&
        a.asInstanceOf[SearchAgent].capabilities.contains(task.queryType)
    }
    val msg = new AgentQueryMsg(this, constraintFunc)
    sendMessage(YellowPages, msg)
    receiveWithin(500) {
      case x: AgentListMsg =>
        speak("Received list with %d agents".format(x.getAgents.length))
        searchAgents ++= x.getAgents.asInstanceOf[List[SearchAgent]]
      case TIMEOUT =>
        speak("No answer.")
    }
  }


  /**
   * Method can be invoked to send queries after the getSearchAgents
   */
  def sendQueries {
    val query = task.query
    val token = task.token
    val typeOfQuery = task.queryType
    searchAgents.foreach {
      val msg = new QueryMessage(this, token, query, typeOfQuery)
      sendMessage(_, msg)
    }
  }



}



