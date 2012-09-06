package pl.edu.pw.elka.stud.tkogut.searchengine

import java.net.{URI, URLConnection}
import java.io.{InputStreamReader, BufferedReader}
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import collection.mutable.ArrayBuffer

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  04.07.12
 * Time:  18:47
 *
 */

import net.liftweb.json.JArray
import net.liftweb.json.JsonAST._

object ArnetMinerGate {

  /**
   * Parameters describing author
   */
  val CONTACTINFOID: String = "ContactInfoId"
  val ALIAS = "Alias"
  val PHONE = "Phone"
  val FAX = "Fax"
  val EMAIL = "Email"
  val HOMEPAGE = "Homepage"
  val POSITION = "Position"
  val AFFILIATION = "Affiliation"
  val ADDRESS = "Address"
  val PHDUNIV = "Phduniv"
  val PHDMAJOR = "Phdmajor"
  val PHDDATE = "Phddate"
  val BIO = "Bio"
  val PICTUREURL = "PictureUrl"
  val INTERESTS = "Interests"
  val PUBCOUNT = "PubCount"
  val CITATIONNUM = "CitationNum"
  val HINDEX = "Hindex"
  val ACTIVITY = "Activity"
  val UPTREND = "Uptrend"
  val NEWSTARSCORE = "NewStarScore"
  val LONGEVITY = "Longevity"
  val DIVERSITY = "Diversity"
  val SOCIABILITY = "Sociability"
  val GINDEX = "Gindex"
  val MSUNIV = "Msuniv"
  val MSDATE = "Msdate"
  val MSMAJOR = "Msmajor"
  val BSUNIV = "Bsuniv"
  val BSMAJOR = "Bsmajor"
  val BSDATE = "Bsdate"

  /**
   * Parameters describing publications
   */
  val ABS = "Abs"
  val JCONFNAME = "Jconfname"
  val AUTHORS = "Authors"
  val PAGES = "Pages"
  val PUBYEAR = "Pubyear"
  val AUTHORIDS = "AuthorIds"
  val STARTPAGE = "Startpage"
  val ENDPAGE = "Endpage"
  val TITLE = "Title"
  val PUBKEY = "Pubkey"
  val CITEDBY = "Citedby"


  /**
   * Common parameters
   */
  //For all types: Conference,authors and publications
  val ID = "Id"

  //For author and conference
  val NAME: String = "Name"


  def getJsonResponse(httpAdress: String): String = {
    val urlGoogleSearch = new java.net.URL(httpAdress)
    val connection: URLConnection = urlGoogleSearch.openConnection
    val in = new BufferedReader(new InputStreamReader(connection.getInputStream));
    var tmpLine: String = in.readLine()
    val jsonDocumentSB = new StringBuilder
    while (tmpLine != null) {
      jsonDocumentSB.append(tmpLine)
      jsonDocumentSB.append("\n")
      tmpLine = in.readLine()
    }
    in.close();
    val jsonRespones = jsonDocumentSB.mkString
    return jsonRespones
  }


}


class ArnetMinerGate {

  def searchPublication(): List[Map[String, String]] = {
    val jsonDocumentParsed = getParsedJsonFromNet(getPublicationString)
    var ret: List[Map[String, String]] = null
    jsonDocumentParsed.children.foreach {
      x =>
        x match {
          case JField("TimeElapsed", JDouble(time)) => _lastSearchTime = time
          case JField("TotalResultCount", JInt(nr)) => _lastSearchpossibleNrOfResults = nr
          case JField("Results", JArray(results)) => ret = processPublicationsResults(results)
          case _ =>
        }
    }
    return ret
  }

  def searchExpert(): List[Map[String, String]] = {
    val jsonDocumentParsed = getParsedJsonFromNet(getExpertString)
    var ret: List[Map[String, String]] = null
    jsonDocumentParsed.children.foreach {
      x =>
        x match {
          case JField("TimeElapsed", JDouble(time)) => _lastSearchTime = time
          case JField("TotalResultCount", JInt(nr)) => _lastSearchpossibleNrOfResults = nr
          case JField("Results", JArray(results)) => ret = processExpertResults(results)
          case _ =>
        }
    }
    return ret
  }

  /**
   * This method is still not ready so should not be used
   */
  private def searchConferences() {
    throw new NotImplementedException
    val jsonDocumentParsed = getParsedJsonFromNet(getConferenceString)
    jsonDocumentParsed.children.foreach {
      x =>
        x match {
          case JField("TimeElapsed", JDouble(time)) => _lastSearchTime = time
          case JField("TotalResultCount", JInt(nr)) => _lastSearchpossibleNrOfResults = nr
          case JField("Results", JArray(results)) => processConferencesResults(results)
          case _ =>
        }
    }
  }

  def lastSearchPossibleNrOfResults = _lastSearchpossibleNrOfResults

  def lastSearchTime = _lastSearchTime

  private def getExpertString: String = {
    return getString("/services/search-expert")
  }

  private def getConferenceString: String = {
    return getString("/services/search-conference")
  }

  private def getPublicationString: String = {
    return getString("/services/search-publication")
  }

  private def processExpertResults(a: List[JValue]): List[Map[String, String]] = {
    val experts = ArrayBuffer.empty[Map[String, String]]
    a.foreach {
      p =>
        val expertData = scala.collection.mutable.Map.empty[String, String]
        p match {
          case JObject(r: List[_]) => r.foreach {
            x =>
              try {
                import ArnetMinerGate._
                x match {
                  case JField(ID, JInt(y)) => y
                  case JField(CONTACTINFOID, JInt(y)) => y
                  case JField(NAME, JString(y)) => expertData += NAME -> y
                  case JField(ALIAS, JArray(y: List[_])) => y.asInstanceOf[List[String]]
                  case JField(PHONE, JString(y)) => expertData += PHONE -> y
                  case JField(FAX, JString(y)) => y
                  case JField(EMAIL, JString(y)) => expertData += (EMAIL -> y)
                  case JField(HOMEPAGE, JString(y)) => if (y.trim.nonEmpty) expertData += HOMEPAGE -> y
                  case JField(POSITION, JString(y)) => y
                  case JField(AFFILIATION, JString(y)) => y
                  case JField(ADDRESS, JString(y)) => expertData += ADDRESS -> y
                  case JField(PHDUNIV, JString(y)) => y
                  case JField(PHDMAJOR, JString(y)) => y
                  case JField(PHDDATE, JString(y)) => y
                  case JField(BIO, JString(y)) => y
                  case JField(PICTUREURL, JString(y)) => if (!y.endsWith("no_photo.jpg")) expertData+= PICTUREURL -> y.toString
                  case JField(INTERESTS, JString(y)) => y
                  case JField(PUBCOUNT, JInt(y)) => y
                  case JField(CITATIONNUM, JInt(y)) => expertData += CITATIONNUM -> y.toString
                  case JField(HINDEX, JInt(y)) => expertData += HINDEX -> y.toString
                  case JField(ACTIVITY, JInt(y)) => y
                  case JField(UPTREND, JInt(y)) => y
                  case JField(NEWSTARSCORE, JInt(y)) => y
                  case JField(LONGEVITY, JInt(y)) => y
                  case JField(DIVERSITY, JInt(y)) => y
                  case JField(SOCIABILITY, JInt(y)) => y
                  case JField(GINDEX, JInt(y)) => y
                  case JField(MSUNIV, JString(y)) => expertData += MSUNIV -> y
                  case JField(MSDATE, JString(y)) => y
                  case JField(MSMAJOR, JString(y)) => y
                  case JField(BSUNIV, JString(y)) => y
                  case JField(BSMAJOR, JString(y)) => y
                  case JField(BSDATE, JString(y)) => y
                  case y: Any => println("Unknown attribute for publication:" + y)
                }
              }
              catch {
                case l: scala.MatchError => println(l)
              }
          }
          case _ =>
        }
        experts += expertData.toMap
    }
    return experts.toList
  }

  private def processPublicationsResults(a: List[JValue]): List[Map[String, String]] = {
    val publications = ArrayBuffer.empty[Map[String, String]]
    a.foreach {
      p =>
        p match {
          case JObject(r: List[_]) =>
            val publicationData = scala.collection.mutable.Map.empty[String, String]
            r.foreach {
              x =>
                try {
                  import ArnetMinerGate._
                  x match {
                    case JField(ABS, JString(y)) => publicationData += ABS -> y
                    case JField(JCONFNAME, JString(y)) => y
                    case JField(AUTHORS, JString(y)) => publicationData += AUTHORS -> y
                    case JField(PAGES, JInt(y)) => y
                    case JField(PUBYEAR, JInt(y)) => publicationData += PUBYEAR -> y.toString
                    case JField(AUTHORIDS, JArray(y: List[_])) => y.asInstanceOf[List[Int]]
                    case JField(STARTPAGE, JInt(y)) => y
                    case JField(ENDPAGE, JInt(y)) => y
                    case JField(ID, JInt(y)) => y
                    case JField(TITLE, JString(y)) => publicationData += TITLE -> y
                    case JField(PUBKEY, JString(y)) => y
                    case JField(CITEDBY, JInt(y)) => publicationData += CITEDBY -> String.valueOf(y)
                    case y: Any => println("Unknown attribute for author:" + y)
                  }
                }
                catch {
                  case l: scala.MatchError => println(l)
                }
            }
            publications += publicationData.toMap
          case _ =>
        }
    }
    return publications.toList
  }

  private def processConferencesResults(a: List[JValue]) {
    a.foreach {
      _ match {
        case JObject(r: List[_]) => r.foreach {
          x =>
            try {
              import ArnetMinerGate._
              x match {
                case JField(ID, JInt(y)) => y
                case JField(NAME, JString(y)) => y
                case JField(m, n) => println("Unknown attribute for conference:" + m + "-" + n)
              }
            }
            catch {
              case l: scala.MatchError => println(l)
            }
        }
        case _ =>
      }
    }
  }

  private def getString(path: String): String = {
    val uri = new URI(
      "http",
      "arnetminer.org",
      path,
      """q=%s&u=oyster&start=0&num=%d""".format(query, resultsLimit),
      null);
    return uri.toASCIIString
  }

  private def getParsedJsonFromNet(adress: String): JValue = {
    val jsonRes = ArnetMinerGate.getJsonResponse(adress)
    val jsonDocumentParsed: _root_.net.liftweb.json.JValue = net.liftweb.json.parse(jsonRes)
    return jsonDocumentParsed
  }

  var query: String = ""
  var resultsLimit: Int = 300
  private var _lastSearchTime: Double = 0.0
  private var _lastSearchpossibleNrOfResults: BigInt = 0

}