package pl.edu.pw.elka.stud.tkogut.passim

import executors.{UniversityData, PublicationInfo, PersonInfo}
import java.util
import pl.edu.pw.elka.stud.tkogut.agentplatform.Agent
import pl.edu.pw.elka.stud.tkogut.brokering.Broker
import pl.edu.pw.elka.stud.tkogut.brokering.message.{InformationMessage, QueryMessage}
import pl.edu.pw.elka.stud.tkogut.agentplatform.message.{AgentListMsg, AgentQueryMsg}
import pl.edu.pw.elka.stud.tkogut.agentplatform.yellowpages.YellowPages
import util.concurrent.CountDownLatch
import pl.edu.pw.elka.stud.tkogut.brokering.dialect.Entity

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut@hotmail.com
 * Date:  22.06.12
 * Time:  20:10
 *
 */

class DummyClient(name: String, queryText: String, whatFor: Entity) extends Agent(name, "Dummy client") {

  private[passim] val tokens = scala.collection.mutable.HashMap.empty[util.UUID, String]

  /**
   * Gets broker and send query
   */
  override def act() = {
    speak("I was born:" + this.getName)
    Thread.sleep(2000)
    val broker: Option[Broker] = getBrokerAgent
    broker match {
      case Some(broker) =>
        speak("Got broker contact")
        val query = new QueryMessage(this, util.UUID.randomUUID(), queryText, whatFor)
        tokens += (query.token -> queryText)
        speak("Asking for:" + whatFor + "->" + queryText)
        sendMessage(broker, query)
      case None => true
    }
  }


  private def getBrokerAgent: Option[Broker] = {
    def constraintFunc(r: Agent): Boolean = {
      return r.isInstanceOf[Broker]
    }
    var ret: Option[Broker] = None
    speak("Trying to get broker")
    val msg = new AgentQueryMsg(this, constraintFunc)
    sendMessage(YellowPages, msg)
    receive {
      case x: AgentListMsg =>
        speak("Received %d broker agent adresses".format(x.getAgents.length))
        if (x.getAgents.nonEmpty) {
          if (x.getAgents.head.isInstanceOf[Broker]) {
            ret = Some(x.getAgents.head.asInstanceOf[Broker])
          }
        }
    }
    return ret
  }


  this.start()
}

class DummyPersonClient(name: String, queryText: String)
  extends DummyClient(name, queryText, PassimDialect.PERSON) {

  private var result: List[PersonInfo] = null
  private var latch = new CountDownLatch(1)

  override def act() = {
    super.act()
    receive {
      case info: InformationMessage =>
        speak("Got answers for query:" + tokens(info.token))
        result = info.information.asInstanceOf[List[PersonInfo]]
        latch.countDown()
      //Logger.info(info.information.mkString("\n"))
    }
  }

  def getResults: List[PersonInfo] = {
    latch.await()
    result
  }
}

class DummyPublicationClient(name: String, queryText: String)
  extends DummyClient(name, queryText, PassimDialect.PUBLICATION) {


  speak("DummyPublicationClient, I am alive")

  private var result: List[PublicationInfo] = null
  private var latch = new CountDownLatch(1)

  def getResults: List[PublicationInfo] = {
    latch.await()
    result
  }

  override def act() = {
    super.act()

    receive {
      case info: InformationMessage =>
        speak("Got answers for query:" + tokens(info.token))
        result = info.information.asInstanceOf[List[PublicationInfo]]
        latch.countDown()
    }
  }

}

class DummyUniveristyClient(name: String, queryText: String)
  extends DummyClient(name, queryText, PassimDialect.UNIVERSITY) {

  private var result: List[UniversityData] = null
  private var latch = new CountDownLatch(1)

  def getResults: List[UniversityData] = {
    latch.await()
    result
  }


  override def act() = {
    super.act()
    receive {
      case info: InformationMessage =>
        speak("Got answers for query:" + tokens(info.token))
        result = info.information.asInstanceOf[List[UniversityData]]
        latch.countDown()
    }
  }
}