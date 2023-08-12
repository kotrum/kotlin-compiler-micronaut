package com.compiler.server.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonValue
import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.Nullable
import io.micronaut.serde.annotation.Serdeable.Deserializable

@Introspected
@Deserializable
@JsonIgnoreProperties(ignoreUnknown = true)
data class Project(
  @Nullable val args: String = "",
  val files: List<ProjectFile> = listOf(),
  @Nullable val confType: ProjectType = ProjectType.JAVA
)

@Introspected
@Deserializable
@JsonIgnoreProperties(ignoreUnknown = true)
data class ProjectFile(val text: String = "", val name: String = "")

@Introspected
@Deserializable
enum class ProjectType(@JsonValue val id: String) {
  JAVA("java"),
  JUNIT("junit"),
  CANVAS("canvas"),
  JS("js"),
  JS_IR("js-ir"),
  WASM("wasm");

  fun isJsRelated(): Boolean = this == JS || this == JS_IR || this == CANVAS
}