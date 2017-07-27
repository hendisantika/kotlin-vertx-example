package com.hendisantika.vertx.standalone.webserver.verticles

import com.hendisantika.vertx.standalone.webserver.JSON_CONT_TYPE
import com.hendisantika.vertx.standalone.webserver.WEB_SRV_PORT
import com.hendisantika.vertx.standalone.webserver.reqhandler.DefaultHandler
import com.hendisantika.vertx.standalone.webserver.reqhandler.FailingHandler
import com.hendisantika.vertx.standalone.webserver.reqhandler.JsonConsumer
import com.hendisantika.vertx.standalone.webserver.reqhandler.SpecialHandler
import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.ResponseContentTypeHandler
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.handler.TemplateHandler
import io.vertx.ext.web.templ.ThymeleafTemplateEngine
import org.slf4j.LoggerFactory

/**
 * Created on 07.04.2017.
 * @author: hendisantika
 *
 * Verticle to start a Webserver with different routes
 */
class WebVerticle(val port: Int = WEB_SRV_PORT) : AbstractVerticle() {

    companion object {
        private val LOG = LoggerFactory.getLogger(WebVerticle::class.java)
    }

    init {
        LOG.debug("WebVerticle created")
    }

    override fun start() {
        LOG.debug("WebVerticle start called; Going to register Router")
        val eventBus = vertx.eventBus()
        val router = Router.router(vertx)
        //These are optional but might be necessary in other routes
        router.route().handler(BodyHandler.create())
        //router.route().handler(CookieHandler.create())
        router.route().failureHandler {
            it.response().setStatusCode(501).end("Sorry but I failed")
        }
        //Adds Content-Type Header
        router.route().handler(ResponseContentTypeHandler.create())

        router.route(HttpMethod.GET, "/").produces(JSON_CONT_TYPE).handler(DefaultHandler(eventBus))
        router.route(HttpMethod.GET, "/special/*").produces(JSON_CONT_TYPE).handler(SpecialHandler())
        router.route(HttpMethod.POST, "/special/:quest").produces(JSON_CONT_TYPE).handler(SpecialHandler())
        router.route(HttpMethod.POST, "/jsonconsume").consumes(JSON_CONT_TYPE).produces(JSON_CONT_TYPE).handler(JsonConsumer())
        router.route(HttpMethod.GET, "/error/*").handler(FailingHandler())

        //Default: static file dir is 'webroot'
        router.route("/static/*").handler(StaticHandler.create())
        //Default: dynamic file dir is 'templates'
        router.route("/dynamic/*").handler { context ->
            context.put("foo", "fooValue was added by different handler!")
            context.next()
        }
        val templateEngine = ThymeleafTemplateEngine.create()
        router.route("/dynamic/*").handler(TemplateHandler.create(templateEngine))

        vertx.createHttpServer().requestHandler({ router.accept(it) }).listen(port, {
            LOG.info("WebServer listening on $port")
        })
    }


}