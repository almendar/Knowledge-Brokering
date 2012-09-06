package pl.edu.pw.elka.stud.tkogut.searchengine.citeseer

import org.apache.http.params.HttpParams
import org.apache.http.params.SyncBasicHttpParams
import org.apache.http.params.HttpProtocolParams
import org.apache.http.HttpVersion
import org.apache.http.protocol.HttpProcessor
import org.apache.http.protocol.ImmutableHttpProcessor
import org.apache.http.HttpRequestInterceptor
import org.apache.http.protocol.RequestContent
import org.apache.http.protocol.RequestTargetHost
import org.apache.http.protocol.RequestConnControl
import org.apache.http.protocol.RequestUserAgent
import org.apache.http.protocol.RequestExpectContinue
import org.apache.http.protocol.HttpRequestExecutor
import org.apache.http.protocol.BasicHttpContext
import org.apache.http.HttpHost
import org.apache.http.impl.DefaultHttpClientConnection
import org.apache.http.impl.DefaultConnectionReuseStrategy
import org.apache.http.protocol.ExecutionContext
import java.net.Socket
import org.apache.http.message.BasicHttpEntityEnclosingRequest
import org.apache.http.HttpResponse
import org.apache.http.HttpEntity
import org.apache.http.util.EntityUtils

/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  30.05.12
 * Time:  20:46
 *
 */

class CiteSeerHtmlDownloader {
  val params: HttpParams = new SyncBasicHttpParams();
  HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
  HttpProtocolParams.setContentCharset(params, "UTF-8");
  HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
  HttpProtocolParams.setUseExpectContinue(params, true);
  val httpproc: HttpProcessor = new ImmutableHttpProcessor(Array[HttpRequestInterceptor](
    // Required protocol interceptors
    new RequestContent(),
    new RequestTargetHost(),
    // Recommended protocol interceptors
    new RequestConnControl(),
    new RequestUserAgent(),
    new RequestExpectContinue()));
  val httpexecutor: HttpRequestExecutor = new HttpRequestExecutor();
  val context = new BasicHttpContext(null);
  val host = new HttpHost("citeseerx.ist.psu.edu", 80);
  val conn = new DefaultHttpClientConnection();
  val connStrategy = new DefaultConnectionReuseStrategy();
  context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
  context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);


  //   def downloadSite(requestParameters : ArrayBuffer[NameValuePair]) : String  =
  //   {
  //     val encodedString = URLEncodedUtils.format(requestParameters, HTTP.DEF_CONTENT_CHARSET.displayName())
  //     return downloadSite("/search?" + encodedString)
  //   }


  /**
   * Download site from CiteSeerX. This open new connection.
   * @param requestString String that will be attached to the citeseer adres e.g.
   *                      /viewdoc/summary;jsessionid=2C33AF829B75EDF34EAF51C3AA23B445?doi=10.1.1.155.2813
   * @return Content of the page, raw html.
   */
  def downloadSite(requestString: String): String = {
    if (!conn.isOpen) {
      val socket: Socket = new Socket(host.getHostName, host.getPort)
      conn.bind(socket, params)
    }
    var request: BasicHttpEntityEnclosingRequest = new BasicHttpEntityEnclosingRequest("GET", requestString)
    request.setParams(params)
    httpexecutor.preProcess(request, httpproc, context)
    var response: HttpResponse = httpexecutor.execute(request, conn, context)
    response.setParams(params)
    httpexecutor.postProcess(response, httpproc, context)
    var responseEntity: HttpEntity = response.getEntity
    var responseString: String = EntityUtils.toString(responseEntity)
    EntityUtils.consume(responseEntity)

    if (!connStrategy.keepAlive(response, context)) {
      conn.close();
    }
    return responseString
  }

  def closeConnection() {
    if (conn.isOpen)
      conn.close();
  }
}
