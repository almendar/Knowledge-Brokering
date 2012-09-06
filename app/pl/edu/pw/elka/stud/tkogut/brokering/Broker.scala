package pl.edu.pw.elka.stud.tkogut.brokering


import pl.edu.pw.elka.stud.tkogut.brokering.dialect._
import pl.edu.pw.elka.stud.tkogut.agentplatform.Agent
import message.{InformationMessage, SearchResultMessage, QueryMessage}
import task.TaskManager


/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut@hotmail.com
 * Date:  20.06.12
 * Time:  18:06
 *
 */

object Broker {
  final val NAME = "Broker"
  final val DESC = """Agent responsible for matching request with search sources"""
}

abstract class Broker extends Agent(Broker.NAME, Broker.DESC) {

  val taskManger: TaskManager
  val dialect: Dialect

  def act() {
    speak("Started to act");
    while (!isRegistered) {
      registerInYellowPages()
    }
    //Thread.sleep(1500)
    //getSearchAgents();
    loop {
      receive {
        case searchQuery: QueryMessage =>
          speak("Got text query with text: %s".format(searchQuery.query))
          val newTaskUUID = taskManger.createNewTask(searchQuery)
          taskManger.startTaskExecution(newTaskUUID)
        case results: InformationMessage =>
          val token = results.token
          val contact = taskManger.getTaskContact(token)
          val forwardMsg = new InformationMessage(this,token,results.information)
          sendMessage(contact,forwardMsg)
      }
    }
  }
}
