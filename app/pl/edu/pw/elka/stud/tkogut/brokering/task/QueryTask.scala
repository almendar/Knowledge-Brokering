package pl.edu.pw.elka.stud.tkogut.brokering.task

import java.util
import scala.collection.mutable.ArrayBuffer
import pl.edu.pw.elka.stud.tkogut.brokering.result.ResultDescription
import pl.edu.pw.elka.stud.tkogut.brokering.{SearchAgent}
import pl.edu.pw.elka.stud.tkogut.brokering.dialect.Entity


/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut@hotmail.com
 * Date:  22.06.12
 * Time:  22:55
 *
 */

class QueryTask(token: util.UUID) extends Task(token) {
  var queryType:Entity = null
  var query: String = null
  def getToken = token
}
