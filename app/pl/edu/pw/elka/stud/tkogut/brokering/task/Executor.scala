package pl.edu.pw.elka.stud.tkogut.brokering.task

import actors.Actor
import pl.edu.pw.elka.stud.tkogut.agentplatform.Agent

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  17.07.12
 * Time:  20:15
 *
 */

object PoisonPill


abstract class Executor(task:Task,name:String = "TaskExecutor", desc:String="Executes Tasks") extends Agent(name,desc) {
  def execute() = {
    this.start()
  }

  def onCancel();

  def reaction : PartialFunction[Any,Unit] = {
    case PoisonPill =>
      onCancel()
      exit()
  }
}
