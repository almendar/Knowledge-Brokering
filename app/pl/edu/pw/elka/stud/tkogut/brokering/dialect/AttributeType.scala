package pl.edu.pw.elka.stud.tkogut.brokering.dialect
import scala.Enumeration

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  23.06.12
 * Time:  10:10
 *
 */
object AttributeType extends Enumeration {
  type AttributeType = Value
  val NAME, ADDRESS, GRAD, PHONE_NUMBER, URL, EMAIL, DATE_TIME,
  INTEGER, FLOAT, STRING = Value
}
