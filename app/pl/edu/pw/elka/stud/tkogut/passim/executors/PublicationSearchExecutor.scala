package pl.edu.pw.elka.stud.tkogut.passim.executors

import pl.edu.pw.elka.stud.tkogut.brokering.result.{SingleResult, ResultDescription, InformationSnip}
import pl.edu.pw.elka.stud.tkogut.brokering.task.QueryTask
import pl.edu.pw.elka.stud.tkogut.brokering.Broker
import collection.mutable
import pl.edu.pw.elka.stud.tkogut.passim.PassimDialect
import pl.edu.pw.elka.stud.tkogut.brokering.message.InformationMessage

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  05.08.12
 * Time:  10:55
 *
 */

class PublicationInfo extends InformationSnip {
  var title:String = null
  var authors:List[String] = null
  var pubYear : String = null
  var citedBy:String = null

  def getCitationNumber : Int = {
    if (citedBy!=null)
    {
      try {
        citedBy.trim.toInt
      } catch {
        case e: NumberFormatException => -Integer.MAX_VALUE
      }
    }
    else {
      -Integer.MAX_VALUE
    }
  }

}

class PublicationSearchExecutor(task: QueryTask, broker:Broker) extends SearchTaskExecutor(task,broker) {


  def act() {
    getSearchAgents()
    sendQueries
    while (searchAgents.size != resultsGather.length) {
      receive(reaction)
    }
    speak("PublicationSearchExecutor:Got all answeres. Results: %d".format(resultsGather.length))
    speak("Started merging results")

    val all = mutable.HashMap.empty[String,PublicationInfo]
    resultsGather.foreach {
      x: ResultDescription =>
        x.r.foreach { y : SingleResult =>
          var pubinfo = new PublicationInfo
          y.getValue(PassimDialect.publicationTitle) match {
            case Some(titleRaw) =>
              var title = titleRaw
              if(title.endsWith("."))
                title = title.substring(0, title.length-1)
              if (all.contains(title)) pubinfo = all(title.toLowerCase)
              else all(title.toLowerCase) = pubinfo
              pubinfo.title = title
              y.getValue(PassimDialect.publicationAuthor) match {
                case Some(author) =>
                  pubinfo.authors = author.split(",").toList
                case None =>
              }
              y.getValue(PassimDialect.publicationYear) match {
                case Some(year) =>
                  if(year!=null && year.trim!="Unknown" && year.trim!="")
                    pubinfo.pubYear = year
                case None =>
              }

              y.getValue(PassimDialect.publicationCitationsNumber) match {
                case Some(citationNumber) =>
                  pubinfo.citedBy = citationNumber
                case None =>
              }
            case None => //skip
          }
        }
    }

    speak("Work done")
    val infMsg = new InformationMessage(this,task.token,all.values.toList)
    sendMessage(broker,infMsg)
  }
}
