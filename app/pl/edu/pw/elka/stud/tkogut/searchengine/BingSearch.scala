package pl.edu.pw.elka.stud.tkogut.searchengine

import java.util.{Calendar, Date}
import xml.Node
import java.net.URL
import collection.mutable.ArrayBuffer

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  24.06.12
 * Time:  19:20
 *
 */

case class BingResult(url: URL, title: String, description: String, date: Date)

class BingSearch(app_id: String) {
  final val BING_URL_START = "http://api.bing.net/xml.aspx?Appid="

  //final val APP_ID = "8A4C8362BAF8F435BCF3F8854CBEF493006E398A"
  private def getSearchURL(query: String): String = BING_URL_START + app_id + "&query=" + query + "&sources=web"

  private val result = ArrayBuffer[BingResult]()

  def search(lookUpQuery: String): List[BingResult] = {
    result.clear
    val respondXML = getDataFromWeb(lookUpQuery)
    processXMLResponse(respondXML)
    return result.toList
  }

  private def getDataFromWeb(lookUpQuery: String): scala.xml.Elem = {
    val fullQueryAdress = getSearchURL(lookUpQuery)
    val respondXML = scala.xml.XML.load(fullQueryAdress)
    respondXML
  }

  private def processXMLResponse(respondXML: scala.xml.Elem): Unit = {

    def getDateTime(timeText: String) = {
      val searchResultDate: Calendar = Calendar.getInstance();
      val year = (timeText.substring(0, 4)) toInt
      val month = (timeText.substring(5, 7)) toInt
      val day = (timeText.substring(8, 10)) toInt
      val hour = (timeText.substring(11, 13)) toInt
      val minutes = (timeText.substring(14, 16)) toInt;
      val seconds = (timeText.substring(17, 19)) toInt;
      searchResultDate.set(year, month, day, hour, minutes, seconds)
      searchResultDate.getTime()
    }

    val webResults = respondXML \\ "WebResult"
    for (webSingleResult: Node <- webResults) {
      val title = (webSingleResult \ "Title") text
      val description = (webSingleResult \ "Description") text
      val url = new URL((webSingleResult \ "Url") text)
      val timeText = ((webSingleResult \ "DateTime") text)
      val date = getDateTime(timeText)
      val bsr = new BingResult(url, title, description, date)
      this.result += bsr
    }
  }
}
