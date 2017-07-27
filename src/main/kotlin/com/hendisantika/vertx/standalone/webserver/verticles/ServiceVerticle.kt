package com.hendisantika.vertx.standalone.webserver.verticles

import com.hendisantika.vertx.standalone.webserver.ACTION_WEB_REQ_RECEIVED
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created on 07.04.2017.
 * @author: hendisantika
 */

class ServiceVerticle : AbstractVerticle() {
    private companion object {
        private val LOG:Logger = LoggerFactory.getLogger(ServiceVerticle::class.java)
    }

    override fun start() {
        vertx.eventBus().consumer<JsonObject>(ACTION_WEB_REQ_RECEIVED, { msg ->
            LOG.debug("Message ${msg.body()} received!")
            val serviceResp = msg.body()

            //some blocking
            vertx.executeBlocking<Any>({ future ->
                Thread.sleep(10_000)
                future.complete("Thread finished")
            }, false, { res ->
                LOG.info("Blocking has finished: ${res.result()}")
                serviceResp.put("serviceVerticleResp", "answering ACTION event from blocking")
                LOG.debug("Reply Message from blocking $serviceResp")
                msg.reply(serviceResp)
            })

//            serviceResp.put("serviceVerticleResp","answering ACTION event")
//            LOG.debug("Reply Message $serviceResp")
//            msg.reply(serviceResp)
        }).completionHandler { res -> LOG.debug("Consumer for '${ACTION_WEB_REQ_RECEIVED}' registered: $res") }
    }
}