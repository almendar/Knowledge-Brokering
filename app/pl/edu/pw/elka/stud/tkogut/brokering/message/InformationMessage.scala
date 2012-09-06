package pl.edu.pw.elka.stud.tkogut.brokering.message

import pl.edu.pw.elka.stud.tkogut.agentplatform.message.Message
import pl.edu.pw.elka.stud.tkogut.brokering.result.InformationSnip
import pl.edu.pw.elka.stud.tkogut.agentplatform.Agent
import java.util.UUID

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  30.07.12
 * Time:  22:54
 *
 */

class InformationMessage(from:Agent,token:UUID,val information:List[InformationSnip]) extends Message(from,token)
