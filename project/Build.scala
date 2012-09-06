import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "Knowledge-Brokering"
    val appVersion      = "0.9"

    val appDependencies = Seq(
			"org.apache.httpcomponents" % "httpclient" % "4.1.1",
			"org.apache.httpcomponents" % "httpcore" % "4.2",			
			"org.mongodb" %% "casbah" % "2.4.1",
			"net.liftweb" %% "lift-json" % "2.4",			
			"com.hp.hpl.jena" % "jena" % "2.6.4",
			"com.hp.hpl.jena" % "arq" % "2.8.8",
			"org.scalatest" %% "scalatest" % "1.8" % "test"			
//			"net.liftweb" %% "lift-json" % "2.5"
    )


    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
		testOptions in Test := Nil
      	//templatesImport += "import pl.edu.pw.elka.stud.tkogut.passim.executors.PersonInfo"     
    )

}
