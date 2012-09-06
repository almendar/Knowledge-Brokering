package pl.edu.pw.elka.stud.tkogut.searchengine

import java.util
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FunSuite}
import com.mongodb.casbah.Imports
import scala.collection.JavaConversions._
import collection.mutable

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  02.07.12
 * Time:  18:46
 *
 */

class MongoDblpGateTest extends FunSuite with BeforeAndAfterAll {


  private final  val LIMIT = 50

  val gate = new MongoDblpGate()
  override def beforeAll {
    gate.connect()
  }
  test("Search for publications") {
    val documentsList = gate.searchPublication("java",LIMIT)
    assert(documentsList.forall(_.authors.nonEmpty))
    assert(documentsList.nonEmpty)
    //assert(documentsList.length <= LIMIT)
  }

  test("Search for authors") {
    val documentsList = gate.searchAuthor("Silberschatz",LIMIT)
    println(documentsList.length)
    assert(documentsList.forall(_.authors.nonEmpty))
    assert(documentsList.nonEmpty)
    //assert(documentsList.length <= LIMIT)
  }

  override def afterAll {
    gate.disconnect()
  }
}
