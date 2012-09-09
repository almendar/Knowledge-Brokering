package pl.edu.pw.elka.stud.tkogut.searchengine.citeseer

import collection.mutable.ArrayBuffer

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  24.06.12
 * Time:  20:42
 *
 */

/**
 * Hold constant values
 */
object CiteSeerInfoExtraction {

  final val LINK = "link"
  final val PUB_INFO = "pubinfo"
  final val SNIPPET = "snippet"
  final val PUB_EXTRAS = "pubextras"
  final val PUB_TOOLS = "pubtools"
  final val HREF = "href"
  final val TEXT = "text"
  final val YEAR = "year"
  final val AUTHORS = "authors"
  final val ABSTRACT = "abstract"
  final val PAGER = "pager"
  final val TITLE="title"
  final val DLINK = "dlinks"

  final val regexPattern = new scala.util.matching.Regex(
    """(?s)(?i)<div class=\"result\"[^>]*>(.+?)""" +
      """<div class=\"pubinfo\">(.+?)</div>.+?""" +
      """<div class=\"snippet\">(.+?)</div>.+?""" +
      """<div class=\"pubextras\">(.+?)</div>.+?""" +
      """<div class=\"pubtools\">(.+?)</div>""" +
      """.*?</div>""", LINK, PUB_INFO, SNIPPET, PUB_EXTRAS, PUB_TOOLS)

  final val linkRegex = new scala.util.matching.Regex(
    """(?s)(?i)<a class=\"remove doc_details"\ href=\"(.+?)\">(.+?)</a>""", HREF, TEXT)


  final val pubInfoAuthorRegex = new scala.util.matching.Regex(
    """(?s)(?i)<span class=\"authors\">(.+?)</span>""", AUTHORS
  )
  final val pubInfoYearRegex = new scala.util.matching.Regex(
    """(?s)(?i)<span class=\"pubyear\">.*?(\d{4}).*?</span>""", YEAR
  )


  final val abstractRegex = new scala.util.matching.Regex(
    """(?s)(?i)<div id=\"abstract\">.*?<p>(.+?)</p>.*?</div>""", ABSTRACT
  )


  final val pagerRegex = new scala.util.matching.Regex(
    """(?s)(?i)<div id=\"pager\">.*?<a href=\"(.*?)\">""", PAGER
  ) //<a href=\"(.*?)\>.*?</a></div>
  
  
  final val dLinksRegex = new scala.util.matching.Regex(
     """(?s)(?i)<li>.*?<a escapexml=\"true\" title=\"(.*?)\" href="(.*?)">.*?</a>.*?</li>""",TITLE,DLINK 
  )
  
}

class CiteSeerInfoExtraction(htmlSite: String) {

  val extracts = ArrayBuffer[scala.collection.mutable.Map[String, String]]()
  var pager: Option[String] = None

  import CiteSeerInfoExtraction._

  def extract() {
    val it = regexPattern.findAllIn(htmlSite).matchData
    while (it.hasNext) {
      val data = scala.collection.mutable.Map[String, String]()
      val matcher = it.next()
      val linkText: String = matcher.group(LINK) //.replaceAll("\n","")
      val rawTitle = CiteSeerGate.removeHtmlTags(linkRegex.findFirstMatchIn(linkText).get.group(TEXT).trim())
      data += (TEXT -> rawTitle)
      val hrefPart = linkRegex.findFirstMatchIn(linkText).get.group(HREF).trim()
      data += (HREF -> hrefPart)
      val pubInfoText: String = matcher.group(PUB_INFO)
      data += (PUB_INFO -> pubInfoText)
      val authors = pubInfoAuthorRegex.findFirstMatchIn(pubInfoText).
        map(_.group(AUTHORS).trim.stripPrefix("by").trim).getOrElse("Unknwon")
      data += (AUTHORS -> authors)
      val pubyear = pubInfoYearRegex.findFirstMatchIn(pubInfoText).map(_.group(YEAR)).getOrElse("Unknown")
      data += (YEAR -> pubyear)
      pager = pagerRegex.findFirstMatchIn(htmlSite).map(_.group(PAGER))
      extracts += data
      
    }

  }
}

