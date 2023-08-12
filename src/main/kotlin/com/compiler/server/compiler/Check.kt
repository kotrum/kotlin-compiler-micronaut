package com.compiler.server.compiler

import io.micronaut.web.router.Router
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class RouteLogger(private val router: Router) {

    companion object {
        private val log = LoggerFactory.getLogger(RouteLogger::class.java)
    }

    init {
        log.info("Exposing the following routes:")
        for (route in router.uriRoutes()) {
            log.info("Method: ${route.httpMethodName} - URI: ${route.uriMatchTemplate.toPathString()}")
        }
    }
}