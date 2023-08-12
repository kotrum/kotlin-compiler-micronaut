package com.compiler.server.controllers

import com.compiler.server.model.Project
import com.compiler.server.model.ProjectType
import com.compiler.server.service.KotlinProjectExecutor
import io.micronaut.core.annotation.Nullable
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*

@Controller("/kotlinServer")
class KotlinPlaygroundRestController(
  private val kotlinProjectExecutor: KotlinProjectExecutor
) {

  @Consumes("*/*")
  @Produces("application/json")
  @Get
  @Post // This will make the method accessible via GET and POST
  fun tryKotlinLangObsoleteEndpoint(
    @QueryValue type: String,
    @Nullable @QueryValue line: Int?,
    @Nullable @QueryValue ch: Int?,
    @Nullable @QueryValue project: Project?
  ): HttpResponse<*> {
    val result = when (type) {
      "getKotlinVersions" -> listOf(kotlinProjectExecutor.getVersion())
      else -> {
        project ?: error("No parameter 'project' found")
        when (type) {
          "run" -> {
            when (project.confType) {
              ProjectType.JAVA -> kotlinProjectExecutor.run(project)
              ProjectType.JS, ProjectType.CANVAS -> kotlinProjectExecutor.convertToJs(project)
              ProjectType.JS_IR -> kotlinProjectExecutor.convertToJsIr(project)
              ProjectType.WASM -> kotlinProjectExecutor.convertToWasm(project)
              ProjectType.JUNIT -> kotlinProjectExecutor.test(project)
            }
          }

          "highlight" -> kotlinProjectExecutor.highlight(project)
          "complete" -> {
            if (line != null && ch != null) {
              kotlinProjectExecutor.complete(project, line, ch)
            } else error("No parameters 'line' or 'ch'")
          }

          else -> error("No parameter 'type' found")
        }
      }
    }
    return HttpResponse.ok(result)
  }
}
