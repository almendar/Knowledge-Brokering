package pl.edu.pw.elka.stud.tkogut.searchengine

import org.scalatest._
/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  04.08.12
 * Time:  21:04
 *
 */

class DbpediaTests extends FunSuite {
  val db = new DbpediaUniversitySearch

  test("Search for Poland") {
    db.search("Poland")
  }




}
