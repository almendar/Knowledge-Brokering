package pl.edu.pw.elka.stud.tkogut.searchengine.citeseer

import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.protocol.HTTP
import scala.collection.JavaConversions._
import scala.collection.mutable.{Map, ArrayBuffer}
import collection.mutable
import java.util

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  24.06.12
 * Time:  20:42
 *
 */
object CiteSeerGate {
  def removeHtmlTags(str: String): String = {
    return str.replaceAll( """(<(\w+)(.*?)>)|(</\w+>)""", "")
  }

  def main(args: Array[String]) {
    val csg = new CiteSeerGate
    csg.setAuthor("Andrew")
    csg.setTitle("Java")
    csg.setYears(2007)
    val i = csg.search(10)
    for (p <- i) {
      val k: Map[String, String] = p;
      println(k)
      println("---------------")
    }
  }
}

class CiteSeerGate {

  private var query = new mutable.StringBuilder()

  def getAbstracts(works: scala.collection.mutable.ArrayBuffer[scala.collection.mutable.Map[String, String]]) {
    works.par.foreach {
      e =>
        val downloader = new CiteSeerHtmlDownloader;
        val articleDetailsHtmlSite = downloader.downloadSite(e(CiteSeerInfoExtraction.HREF))
        val abstractText =
          CiteSeerInfoExtraction.abstractRegex.findFirstMatchIn(articleDetailsHtmlSite)
            .map(_.group(CiteSeerInfoExtraction.ABSTRACT)).getOrElse("Unknown")
        e += (CiteSeerInfoExtraction.ABSTRACT -> abstractText)
        
        val links = CiteSeerInfoExtraction.dLinksRegex.findAllIn(articleDetailsHtmlSite).matchData.map(_.group(CiteSeerInfoExtraction.DLINK)).toList
        println(links.mkString(","))
        
       
        //downloader.closeConnection()
        
        
    }
  }


  def setTitle(title: String) {
    val formatedTitle = if (title.exists(_.isWhitespace)) "title:(%s)".format(title) else "title:" + title
    if (!query.isEmpty) query append "+AND+"
    query append formatedTitle
  }

  def setYears(left: Int = 1899, right: Int = util.Calendar.getInstance().get(util.Calendar.YEAR) + 1) {
    if (!query.isEmpty) query append "+AND+"
    query append "year:[%d+TO+%d]".format(left, right)
  }

  def setAuthor(authorName: String) {
    val author = if (authorName.exists(_.isWhitespace)) "author:(%s)".format(authorName) else "author:" + authorName
    if (!query.isEmpty) query append "+AND+"
    query append author
  }


  //  def searchForPublications(publicationKeyword : String, limit:Int) : List[Map[String, String]] =
  //  {
  //    val requestParameters = ArrayBuffer[NameValuePair]()
  //    requestParameters+=(new BasicNameValuePair("q", publicationKeyword))
  //    requestParameters+=(new BasicNameValuePair("submit", "Search"))
  //    requestParameters+=(new BasicNameValuePair("sort", "rlv"))
  //    requestParameters+=(new BasicNameValuePair("t", "doc"))
  //    return processIt(requestParameters, limit)
  //  }
  //
  //  def searchForAuthors(authorKeyword : String, limit:Int) : List[Map[String, String]] =
  //  {
  //    import scala.runtime.RichChar
  //    var author = if(authorKeyword.exists(_.isWhitespace)) "(%s)".format(authorKeyword) else authorKeyword
  //    val requestParameters = ArrayBuffer[NameValuePair]()
  //    requestParameters+=(new BasicNameValuePair("q", "author:"+author))
  //    requestParameters+=(new BasicNameValuePair("submit", "Search"))
  //    requestParameters+=(new BasicNameValuePair("sort", "rlv"))
  //    requestParameters+=(new BasicNameValuePair("t", "doc"))
  //    return processIt(requestParameters, limit)
  //  }

  def search(limit: Int): List[Map[String, String]] = {
    val requestParameters = ArrayBuffer[NameValuePair]()
    requestParameters += (new BasicNameValuePair("q", query.toString))
    requestParameters += (new BasicNameValuePair("submit", "Search"))
    requestParameters += (new BasicNameValuePair("sort", "rlv"))
    requestParameters += (new BasicNameValuePair("t", "doc"))
    query.clear()
    return processIt(requestParameters, limit)
  }

  def processIt(requestParameters: ArrayBuffer[NameValuePair], limit: Int): scala.List[Map[String, String]] = {
    val encodedString = URLEncodedUtils.format(requestParameters, HTTP.DEF_CONTENT_CHARSET.displayName())
    val fullUrl = "/search?" + encodedString
    //println(fullUrl)
    var nextPagina: Option[String] = Some[String](fullUrl)
    var downloader = new CiteSeerHtmlDownloader;
    var buffer = ArrayBuffer[scala.collection.mutable.Map[String, String]]()
    var results = 0
    while (nextPagina != None && results <= limit) {
      val siteContent = downloader.downloadSite(nextPagina.get)
      val extractor = new CiteSeerInfoExtraction(siteContent)
      extractor.extract()
      nextPagina = extractor.pager
      results += extractor.extracts.length
      //println(extractor.extracts.length)
      buffer ++= extractor.extracts
    }
    val ret: List[Map[String, String]] = buffer.toList
    return ret
  }
}
