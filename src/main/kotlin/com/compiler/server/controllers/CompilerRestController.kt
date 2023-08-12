package com.compiler.server.controllers

import com.compiler.server.model.*
import com.compiler.server.model.bean.VersionInfo
import com.compiler.server.service.KotlinProjectExecutor
import io.micronaut.http.annotation.*
import javax.inject.Inject

@Controller("/api/compiler")
class CompilerRestController @Inject constructor(private val kotlinProjectExecutor: KotlinProjectExecutor) {

  @Post("/run")
  fun executeKotlinProjectEndpoint(@Body project: Project): ExecutionResult {
    return kotlinProjectExecutor.run(project)
  }

  @Post("/test")
  fun testKotlinProjectEndpoint(project: Project): ExecutionResult {
    return kotlinProjectExecutor.test(project)
  }

  @Post("/translate")
  fun translateKotlinProjectEndpoint(
    project: Project,
    @QueryValue(defaultValue = "false") ir: Boolean,
    @QueryValue(defaultValue = "js") compiler: String
  ): TranslationResultWithJsCode {
    if (!ir) {
      return kotlinProjectExecutor.convertToJs(project)
    }
    return when (KotlinTranslatableCompiler.valueOf(compiler.uppercase())) {
      KotlinTranslatableCompiler.JS -> kotlinProjectExecutor.convertToJsIr(project)
      KotlinTranslatableCompiler.WASM -> kotlinProjectExecutor.convertToWasm(project)
    }
  }

  @Post("/complete")
  fun getKotlinCompleteEndpoint(
    project: Project,
    @QueryValue line: Int,
    @QueryValue ch: Int
  ) = kotlinProjectExecutor.complete(project, line, ch)

  @Post("/highlight")
  fun highlightEndpoint(project: Project): Map<String, List<ErrorDescriptor>> =
    kotlinProjectExecutor.highlight(project)
}

@Controller("/versions")
class VersionRestController @Inject constructor(private val kotlinProjectExecutor: KotlinProjectExecutor) {

  @Get
  fun getKotlinVersionEndpoint(): List<VersionInfo> = listOf(kotlinProjectExecutor.getVersion())
}
