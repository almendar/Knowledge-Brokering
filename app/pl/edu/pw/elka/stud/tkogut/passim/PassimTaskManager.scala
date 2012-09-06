package pl.edu.pw.elka.stud.tkogut.passim

import executors.{UniversitySearchExecutor, PublicationSearchExecutor, PersonSearchExecutor}
import scala.collection.mutable
import java.util.UUID
import pl.edu.pw.elka.stud.tkogut.brokering.task.{Executor, QueryTask, TaskManager}
import pl.edu.pw.elka.stud.tkogut.brokering.message.QueryMessage
import pl.edu.pw.elka.stud.tkogut.agentplatform.Agent
import pl.edu.pw.elka.stud.tkogut.brokering.Broker

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  17.07.12
 * Time:  19:42
 *
 */

class PassimTaskManager(owner:Broker) extends TaskManager(owner) {

  val _tokenToTask = mutable.Map.empty[UUID,QueryTask]

  def createNewTask(queryMsg:QueryMessage)  : UUID =
  {
      val task = new QueryTask(queryMsg.token)
      task.query = queryMsg.query
      task.queryType = queryMsg.typeOfQuery
      task.contact = queryMsg.from
    _tokenToTask += (task.token -> task)
    return task.token
  }

  //@TODO
  def cancelTask(token:UUID) {

  }


  def getTaskContact(token:UUID) : Agent = {
    return _tokenToTask(token).contact
  }



  def startTaskExecution(token:UUID) {
    val task = _tokenToTask(token)
    import PassimDialect._
    task.queryType match {
      case PERSON => task.executorList =  List(new PersonSearchExecutor(task,owner))
      case PUBLICATION => task.executorList = List(new PublicationSearchExecutor(task,owner))
      case UNIVERSITY => task.executorList = List(new UniversitySearchExecutor(task,owner))
    }
    task.executorList.foreach(_.execute())
  }
}
