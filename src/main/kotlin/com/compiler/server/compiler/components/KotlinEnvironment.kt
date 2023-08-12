package com.compiler.server.compiler.components

import com.compiler.server.model.bean.LibrariesFile
import component.KotlinEnvironment
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory

@Factory
class KotlinEnvironmentConfiguration(
  private val librariesFile: LibrariesFile,
) {

  @Bean
  fun kotlinEnvironment(): KotlinEnvironment {
    val classPath =
      listOfNotNull(librariesFile.jvm)
        .flatMap {
          it.listFiles()?.toList()
            ?: error("No kotlin libraries found in: ${librariesFile.jvm.absolutePath}")
        }

    val additionalJsClasspath = listOfNotNull(librariesFile.js)
    return KotlinEnvironment(classPath, additionalJsClasspath, listOfNotNull(librariesFile.wasm))
  }
}
