package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import pl.edu.pw.elka.stud.tkogut.agentplatform.{Agent, AgentManagmentPlatform}
import pl.edu.pw.elka.stud.tkogut.passim._
import pl.edu.pw.elka.stud.tkogut.passim.search.google.GoogleSearcher
import pl.edu.pw.elka.stud.tkogut.passim.search.bing.BingSearcherAgent
import pl.edu.pw.elka.stud.tkogut.passim.search.citeseer.CiteSeerSearcherAgent
import pl.edu.pw.elka.stud.tkogut.passim.search.dblp.MongoDblpSearcher
import pl.edu.pw.elka.stud.tkogut.passim.search.arnetminer.ArnetMinerSearcher
import play.api.libs.concurrent.{Promise, Akka}
import play.api.Play.current
import executors.{UniversityData, PublicationInfo, PersonInfo}
import search.dbpedia.DbpediaSearcher


object Application extends Controller {

  val searchForm = Form(
    tuple(
      "searchQuery" -> nonEmptyText,
      "type" -> text
    )
  )

  def index = Action {
    request =>
      AgentManagmentPlatform.startPlatform(List[Agent](
        new PassimBroker,
        new GoogleSearcher,
        new BingSearcherAgent,
        new CiteSeerSearcherAgent,
        new MongoDblpSearcher,
        new ArnetMinerSearcher,
        new DbpediaSearcher
        //new DummyClient
      ));
      Redirect(routes.Application.search)
  }

  def search = Action {
    Ok(views.html.index(searchForm))
  }
  
  
  def authorSearch(author:String) = Action {
   
   val client = new DummyPersonClient(System.currentTimeMillis.toString, author)
   val promise: Promise[List[PersonInfo]] = Akka.future { client.getResults }
   Async {promise.map(i => Ok(views.html.personresults(author, i)))}
  }
  
  
  def universitySearch(university:String) = Action {
	  val client = new DummyUniveristyClient(System.currentTimeMillis.toString, university)
      val promise: Promise[List[UniversityData]] = Akka.future {client.getResults}
	  Async {promise.map(i => Ok(views.html.universityresults(i.sortBy(-_.getEstablishedYear))))
	  }
  }
  	
 
  
  def publicationSearch(publication:String) =  Action {
       val client = new DummyPublicationClient(System.currentTimeMillis.toString, publication)
      val promise: Promise[List[PublicationInfo]] = Akka.future {
        client.getResults
      }
      Async {
        promise.map(i =>
          Ok(views.html.publicationresult(i.sortBy(-_.getCitationNumber)))
        )
      }
  }
  
  

  def newSearch = Action {
    implicit request =>
      searchForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index(errors)), {
        case (qryText, searchType) =>
          searchType match {
            case "Person" =>
              Redirect(routes.Application.authorSearch(qryText))
            case "Publication" =>
              	Redirect(routes.Application.publicationSearch(qryText))
            case "University" =>
              Redirect(routes.Application.universitySearch(qryText))
          }
      }
        //Redirect(routes.Application.search)
      )

  }
  
}