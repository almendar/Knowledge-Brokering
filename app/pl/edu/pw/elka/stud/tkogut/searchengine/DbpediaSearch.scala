package pl.edu.pw.elka.stud.tkogut.searchengine

import com.hp.hpl.jena.query.{QuerySolution, ResultSet, QueryExecutionFactory, QueryFactory}
import scala.collection.mutable
import com.hp.hpl.jena.rdf.model.RDFNode
import java.util.Calendar
import java.util.Formatter.DateTime
import com.hp.hpl.jena.datatypes.xsd.{IllegalDateTimeFieldException, XSDDateTime}
import com.hp.hpl.jena.datatypes.BaseDatatype
import collection.mutable.ArrayBuffer

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  04.08.12
 * Time:  20:30
 *
 */


class UniversityDescription {
  var name:String = null
  var country:String = null
  var city:String = null
  var yearEstablished:Int = 0
  var homepage : String = null

  override def toString() : String = {
    val sb = new mutable.StringBuilder()
    sb append name
    sb append " | "
    sb append city
    sb append " | "
    sb append country
    sb append " | "
    sb append yearEstablished
    sb append " | "
    sb append homepage
    sb.toString()
  }
}


class DbpediaUniversitySearch {

  private final val queryTextCountry =
    """
      | PREFIX owl: <http://www.w3.org/2002/07/owl#>
      | PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
      | PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
      | PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
      | PREFIX foaf: <http://xmlns.com/foaf/0.1/>
      | PREFIX dc: <http://purl.org/dc/elements/1.1/>
      | PREFIX : <http://dbpedia.org/resource/>
      | PREFIX dbpedia2: <http://dbpedia.org/property/>
      | PREFIX dbpedia: <http://dbpedia.org/>
      | PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
      | PREFIX dbpo: <http://dbpedia.org/ontology/>
      |
      | SELECT * WHERE {
      | ?country rdf:type dbpo:Country.
      | ?country foaf:name ?countryName.
      | ?uni     rdf:type dbpo:University.
      | ?uni     dbpedia2:country ?country.
      | ?uni     foaf:name ?uniName.
      | ?uni     dbpedia2:established ?established.
      | ?uni     dbpedia2:city ?city.
      | ?uni     foaf:homepage ?homepage.
      | ?city    foaf:name ?cityName.
      | FILTER (regex(?countryName, "%s","i"))
      | } limit 400
    """.stripMargin


  private final val queryTextCity =
    """
      | PREFIX owl: <http://www.w3.org/2002/07/owl#>
      | PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
      | PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
      | PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
      | PREFIX foaf: <http://xmlns.com/foaf/0.1/>
      | PREFIX dc: <http://purl.org/dc/elements/1.1/>
      | PREFIX : <http://dbpedia.org/resource/>
      | PREFIX dbpedia2: <http://dbpedia.org/property/>
      | PREFIX dbpedia: <http://dbpedia.org/>
      | PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
      | PREFIX dbpo: <http://dbpedia.org/ontology/>
      |
      | SELECT * WHERE {
      | ?country rdf:type dbpo:Country.
      | ?country foaf:name ?countryName.
      | ?city    rdf:type dbpo:City.
      | ?city    foaf:name ?cityName.
      | ?uni     rdf:type dbpo:University.
      | ?uni     foaf:name ?uniName.
      | ?uni     dbpedia2:city ?city.
      | ?uni     dbpedia2:country ?country.
      | ?uni     dbpedia2:established ?established.
      | ?uni     foaf:homepage ?homepage.
      | FILTER (regex(?cityName, "%s","i"))
      | } limit 400
    """.stripMargin


  def search(countryOrCity: String) : List[UniversityDescription] = {
    val formatedQueryCountry = queryTextCountry.format(countryOrCity)
    val formatedQueryCity = queryTextCity.format(countryOrCity)
    val retResults = mutable.ArrayBuffer.empty[UniversityDescription]
    try {
      //Try country search
      var queryConstruct = QueryFactory.create(formatedQueryCountry);
      var qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", queryConstruct);
      var results: ResultSet = qexec.execSelect()
      processResults(results, retResults)
    }
    catch {
      case x:java.lang.ExceptionInInitializerError => println(x.getMessage)
    }
    return retResults.toList
  }

  def processResults(results: ResultSet,retResults: ArrayBuffer[UniversityDescription]) {
    while (results.hasNext) {
      val qs: QuerySolution = results.next()
      val uniName = qs.get("uniName").asLiteral().getString
      val countryName = qs.get("countryName").asLiteral().getString
      val established = qs.get("established").asLiteral().getValue match {
        case y: Calendar => y.get(Calendar.YEAR)
        case x: XSDDateTime =>
          try {
            x.getYears
          } catch {
            case x:IllegalDateTimeFieldException => Int.MinValue
          }
        case z: java.lang.Integer => z.intValue()
        case k: String => "\\d{4}$".r.findFirstIn(k) match {
          case Some(year) => year.toInt
          case None => Int.MinValue
        }
        case l: BaseDatatype.TypedValue => Int.MinValue
      }
      val city = qs.get("cityName").asLiteral().getString

      val homepage = qs.get("homepage").asResource().getURI match {
        case x: String => x
      }
      val ud = new UniversityDescription
      ud.name = uniName
      ud.country = countryName
      ud.yearEstablished = established
      ud.city = city
      ud.homepage = homepage
      retResults += ud
    }
  }
}


