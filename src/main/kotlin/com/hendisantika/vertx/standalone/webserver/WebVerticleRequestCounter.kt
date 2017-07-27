package com.hendisantika.vertx.standalone.webserver

import java.util.concurrent.atomic.AtomicInteger

/**
 * Created on 05.05.2017.
 * @author: hendisantika
 *
 * Top level function to be used by all RequestHandlers for logging purposes
 *
 */
private val counter: AtomicInteger = AtomicInteger(0)

fun incrementAndGetCounter() = counter.incrementAndGet()