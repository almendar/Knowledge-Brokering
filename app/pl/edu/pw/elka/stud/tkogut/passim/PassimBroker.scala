package pl.edu.pw.elka.stud.tkogut.passim

import pl.edu.pw.elka.stud.tkogut.brokering.Broker

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  27.07.12
 * Time:  11:54
 *
 */

class PassimBroker extends Broker {
    val dialect = PassimDialect
    val taskManger = new PassimTaskManager(this)
}
