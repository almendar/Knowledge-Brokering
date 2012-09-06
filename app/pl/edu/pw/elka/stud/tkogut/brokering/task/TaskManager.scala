package pl.edu.pw.elka.stud.tkogut.brokering.task

import pl.edu.pw.elka.stud.tkogut.brokering.message.QueryMessage
import java.util.UUID
import pl.edu.pw.elka.stud.tkogut.agentplatform.Agent

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  17.07.12
 * Time:  16:26
 *
 */

abstract class TaskManager(taskOwner:Agent) {
  def createNewTask(queryMsg:QueryMessage) : UUID
  def cancelTask(token:UUID)
  def startTaskExecution(token:UUID)
  def getTaskContact(token:UUID) : Agent
}