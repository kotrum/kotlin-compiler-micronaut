package com.compiler.server.controllers

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller
class HealthRestController {
  @Get("/health")
  fun healthEndpoint() = "OK"
}
