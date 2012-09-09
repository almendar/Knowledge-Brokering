package pl.edu.pw.elka.stud.tkogut.passim.executors

import pl.edu.pw.elka.stud.tkogut.brokering.task.QueryTask
import pl.edu.pw.elka.stud.tkogut.brokering.Broker
import pl.edu.pw.elka.stud.tkogut.brokering.result.{SingleResult, ResultDescription, InformationSnip}
import pl.edu.pw.elka.stud.tkogut.brokering.message.InformationMessage
import collection.mutable
import pl.edu.pw.elka.stud.tkogut.passim.PassimDialect
import scala.collection.mutable.ArrayBuffer
import java.util.Calendar

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  05.08.12
 * Time:  10:56
 *
 */


class UniversityData extends InformationSnip {
  var name: ArrayBuffer[String] = ArrayBuffer.empty[String]
  var country: String = ""
  var city: String = ""
  var yearEstablished: String = ""
  var homepage: String = ""

  def getEstablishedYear: Int = {
    if (yearEstablished != null) {
      try {
        yearEstablished.trim.toInt
      } catch {
        case e: NumberFormatException => Integer.MAX_VALUE
      }
    }
    else {
      Integer.MAX_VALUE
    }
  }

}

class UniversitySearchExecutor(task: QueryTask, broker: Broker) extends SearchTaskExecutor(task, broker) {

  def act() {
    getSearchAgents()
    sendQueries
    while (searchAgents.size != resultsGather.length) {
      receive(reaction)
    }
    speak("PublicationSearchExecutor:Got all answeres")

    val all = mutable.HashMap.empty[String, UniversityData]
    resultsGather.foreach {
      x: ResultDescription =>
        x.r.foreach {
          y: SingleResult =>

            y.getValue(PassimDialect.universityName) match {
              case Some(name) =>
                val universityHomepage : String =  y.getValue(PassimDialect.universityHomepage).getOrElse("")
                if(universityHomepage.nonEmpty) {
	                val ud = all.getOrElse(universityHomepage, new UniversityData)
	                all(universityHomepage) = ud
	                ud.name += name
	                ud.country = y.getValue(PassimDialect.universityCountry).getOrElse("")
	                ud.city = y.getValue(PassimDialect.universityHomeCity).getOrElse("")
	                ud.homepage = y.getValue(PassimDialect.universityHomepage).getOrElse("")
	                ud.yearEstablished = y.getValue(PassimDialect.universityFoundationYear).getOrElse("0")
	                try {
	                  val year:Int = ud.yearEstablished.toInt
	                	if(! (year < Calendar.getInstance.get(Calendar.YEAR) && year > 800)) 
	                	 ud.yearEstablished = ""
	                }
	                catch {
	                  case e:java.lang.NumberFormatException =>
	                    ud.yearEstablished = ""
	                }
                }
              case None => //skip those withourt  a name
            }
        }
    }
    val infMsg = new InformationMessage(this, task.token, all.values.toList)
    sendMessage(broker, infMsg)
    speak("Work done")
  }
}
