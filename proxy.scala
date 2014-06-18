// Routes for proxy
# Proxy additional services
GET     /api/um/*proxyUrl               com.sentrana.mmcore.controllers.Proxy.getProxyParse(proxyUrl)
POST    /api/um/*proxyUrl               com.sentrana.mmcore.controllers.Proxy.postProxyParse(proxyUrl)

/**
 * Created by graham.harwood on 6/16/2014.
 */

import play.api.Play
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.libs.ws._
import play.api.mvc.{Action, Controller}
import views.html.defaultpages.badRequest

object Proxy extends Controller {


  def getProxyParse(proxyUrl: String) = Action.async { request =>
    var url = buildUrl(request.uri)
    if (url == "") {
      badRequest(null, "Url Not matching proxy possibilities")
    }
    WS.url(url).get().map {

      response => Ok(response.body)
    }
  }

  def postProxyParse(proxyUrl: String) = Action.async { request =>
    val url = buildUrl(request.uri)
    var data = Json.parse(request.body.asText.get);
    if(url ==""){
      badRequest(null, "Url Not matching proxy possibilities")
    }
    WS.url(url).withHeaders(  "Accept" -> "application/json",
      "Cookie" -> ("sessionId=" + request.cookies.apply("sessionId").value)).post(data).map { response =>
      Ok(data)
    }
  }

  def deleteProxyParse(proxyUrl: String) = Action.async { request =>
    var url = buildUrl(proxyUrl)
    if (url == "") {
      badRequest(null, "Url Not matching proxy possibilities")
    }
    WS.url(url).delete().map { response =>
      val contentType = response.header("Content-Type").getOrElse("text/json")
      Ok(response.body)
    }
  }


  private def buildUrl(rawUrl: String) = {
    var url = ""
    url = Play.current.configuration.getString("service.config.loc").getOrElse("/") + rawUrl
    url
  }



}