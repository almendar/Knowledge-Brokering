package pl.edu.pw.elka.stud.tkogut.passim.executors

import pl.edu.pw.elka.stud.tkogut.brokering.result.{SingleResult, ResultDescription, InformationSnip}
import collection.mutable
import pl.edu.pw.elka.stud.tkogut.brokering.task.QueryTask
import pl.edu.pw.elka.stud.tkogut.brokering.Broker
import pl.edu.pw.elka.stud.tkogut.passim.PassimDialect
import pl.edu.pw.elka.stud.tkogut.brokering.dialect.Attribute
import pl.edu.pw.elka.stud.tkogut.brokering.message.InformationMessage


case class PersonPublication(authors: String, title: String, pubYear: String)

class PersonInfo(var name: String) extends InformationSnip {
  var infoSource = mutable.HashSet.empty[String]
  var email: String = null
  var phoneNumber: String = null
  var publications = mutable.HashSet.empty[PersonPublication]
  var websiteUrl: String = null
  var hindex: String = null
  var citationNumber : String = null
  var pictureURL : String = null


  override def toString(): String = {
    val sb = new mutable.StringBuilder()
    sb append name
    sb append " phoneNumber:"
    sb append phoneNumber
    sb append " publications:"
    sb append publications.mkString(",")
    sb append " email:"
    sb append email
    sb append " H-Index:"
    sb append hindex
    sb append "\nData from:"
    sb append infoSource.mkString(",")

    sb.toString
  }

  override def hashCode(): Int = {
    return name.hashCode
  }
}




/**
 * Executes search, gather and merge results for the broker
 * @param task Search task data
 * @param broker Broker for whom the task is done
 */
class PersonSearchExecutor(task: QueryTask, broker: Broker) extends SearchTaskExecutor(task, broker) {

  val tmpAttribs = mutable.HashSet.empty[Attribute]

  def act() {
    getSearchAgents()
    sendQueries
    while (searchAgents.size != resultsGather.length) {
      receive(reaction)
    }
    speak("Got all answeres")

    val authors = mutable.HashMap.empty[String, PersonInfo]
    val authorsDots = mutable.HashMap.empty[String, PersonInfo]
    resultsGather.foreach {
      x: ResultDescription =>
        x.r.foreach {
          y: SingleResult =>
            y.getValue(PassimDialect.personName) match {
              case Some(personName) =>
                val names = personName.split(",")
                names.foreach {
                  z =>
                    var stringName: String = null
                    var dotStringNames: String = null
                    var trimedName: String = z.trim
                    var splitName = trimedName.split(" ") // e.g. ["Donald", "E.", "Knuth"]

                    if (splitName.length > 1) {
                      val surname = splitName.last
                      val names: List[String] = splitName.init.toList

                      //if has a dot in name
                      if (splitName.exists(_.contains("."))) {
                        val namesListWithoutDots = names.filterNot(_.contains("."))
                        //This will give us Donald Knuth
                        if (namesListWithoutDots.nonEmpty) {
                          stringName = (namesListWithoutDots ::: List(surname)).mkString(" ").trim
                        }
                      }
                      val dotNames: List[String] = names.map {
                        x: String =>
                          if (x.length == 2 && x(1) == '.')
                            x
                          else if (x.length > 2)
                            x(0) + "."
                          else x
                      }
                      //this will give D. E. Knuth
                      dotStringNames = (dotNames ::: List(surname)).mkString(" ")
                    }
                    else {
                      stringName = trimedName
                    }

                    var personInfo: PersonInfo = null

                    //1 1
                    if (stringName != null && dotStringNames != null) {

                      if (authors.contains(stringName)) {
                        personInfo = authors(stringName)
                        authorsDots(dotStringNames) = personInfo
                      }
                      else if (authorsDots.contains(dotStringNames)) {
                        personInfo = authorsDots(dotStringNames)
                        personInfo.name = stringName
                        authors(stringName) = personInfo
                      }
                      else {
                        personInfo = new PersonInfo(stringName)
                        authors(stringName) = personInfo
                        authorsDots(dotStringNames) = personInfo
                      }
                    }
                    //0 1
                    else if (stringName == null && dotStringNames != null) {
                      if (authorsDots.contains(dotStringNames)) {
                        personInfo = authorsDots(dotStringNames)
                      }
                      else {
                        personInfo = new PersonInfo(dotStringNames)
                        authorsDots(dotStringNames) = personInfo
                      }
                    }
                    // 1 0
                    else if (stringName != null && dotStringNames == null) {
                      if (authors.contains(stringName)) {
                        personInfo = authors(stringName)
                      }
                      else {
                        personInfo = new PersonInfo(stringName)
                        authors(stringName) = personInfo
                      }
                    }
                    //0 0 never happens
                    //                    else if(stringName==null && dotStringNames==null) {
                    //
                    //                    }

                    y.getValue(PassimDialect.personEmail) match {
                      case Some(email) => personInfo.email = email
                      case None =>
                    }
                    y.getValue(PassimDialect.publicationTitle) match {
                      case Some(title) =>
                        val year = y.getValue(PassimDialect.publicationYear)
                        personInfo.publications += PersonPublication(personName, title, year.getOrElse("1"))
                      case None =>
                    }
                    y.getValue(PassimDialect.personHIndex) match {
                      case Some(hindex) =>
                        personInfo.hindex = hindex
                      case None =>
                    }

                    y.getValue(PassimDialect.personCitationNum) match {
                      case Some(citNum) =>
                        personInfo.citationNumber = citNum
                      case None =>
                    }

                    y.getValue(PassimDialect.personPicture) match {
                      case Some(picture) =>
                        personInfo.pictureURL = picture
                      case None =>
                    }


                    personInfo.infoSource += y.source
                }
              case None =>
            }


        }
    }
    val all = (authors.values.toList ::: authorsDots.values.toList).distinct.sortBy {
      x =>

        val similarity = (((2.0 * (task.query.toSet & x.name.toSet).size))) /// (query.toSet.size + x.name.toSet.size)
        -similarity //want this served more similar firt in order
    }
    val infMsg = new InformationMessage(this, task.token, all)
    sendMessage(broker, infMsg)
    speak("Work done")
  }
}
