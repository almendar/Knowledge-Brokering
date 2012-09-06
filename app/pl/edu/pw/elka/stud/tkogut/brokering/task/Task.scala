package pl.edu.pw.elka.stud.tkogut.brokering.task

import java.util
import pl.edu.pw.elka.stud.tkogut.agentplatform.Agent


/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  30.06.12
 * Time:  10:21
 *
 */

abstract class Task(val token: util.UUID) {
  var contact: Agent = null
  var executorList:List[Executor] = Nil
}
