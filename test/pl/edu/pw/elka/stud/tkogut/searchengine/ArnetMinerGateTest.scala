package pl.edu.pw.elka.stud.tkogut.searchengine

import org.scalatest.FunSuite
import java.util
import pl.edu.pw.elka.stud.tkogut.agentplatform.Agent
import util.concurrent.CountDownLatch
import pl.edu.pw.elka.stud.tkogut.brokering.message.{SearchResultMessage, QueryMessage}
import pl.edu.pw.elka.stud.tkogut.passim.search.arnetminer.ArnetMinerSearcher
import pl.edu.pw.elka.stud.tkogut.agentplatform.yellowpages.YellowPages


class ArnetMinerGateTest extends FunSuite {
  val gate = new ArnetMinerGate


  test("Search Agent test") {
    YellowPages.start()
    val latch = new CountDownLatch(1)
    val agent = new ArnetMinerSearcher
    import pl.edu.pw.elka.stud.tkogut.passim.PassimDialect._
    val a: Agent = new Agent("", "") {
      def act() {
        val uuid = util.UUID.randomUUID()
        val constrainMessage = new QueryMessage(this, uuid, "Stephen", PERSON)
        sendMessage(agent, constrainMessage)
        receive {
          case x: SearchResultMessage =>
            val results = x.getResults
            println(results)
            latch.countDown()
        }

      }
    }
    agent.start()
    a.start()

    latch.await()

  }

  test("Expert search limiting") {
    gate.resultsLimit = 100
    gate.query = "Java"
    //println(gate.searchPublication())
    val res = gate.searchExpert()
    assert(res.length <= 100)


  }

  test("Publication limiting test") {
    gate.resultsLimit = 100
    gate.query = "Silberschatz"
    //println(gate.searchPublication())
    val res = gate.searchPublication()
    assert(res.length <= 100)
  }


}
