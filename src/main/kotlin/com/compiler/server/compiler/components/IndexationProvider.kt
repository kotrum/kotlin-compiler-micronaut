package com.compiler.server.compiler.components

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import model.ImportInfo
import org.slf4j.LoggerFactory
import java.io.File

@Singleton
class IndexationProvider(
  @Value("\${indexes.file}") private val indexesFileName: String,
  @Value("\${indexesJs.file}") private val indexesJsFileName: String
) {
  companion object {
    const val UNRESOLVED_REFERENCE_PREFIX = "Unresolved reference: "
    private val log = LoggerFactory.getLogger(IndexationProvider::class.java)
  }

  private val jvmIndexes: Map<String, List<ImportInfo>>? by lazy {
    initIndexes(indexesFileName)
  }

  private val jsIndexes: Map<String, List<ImportInfo>>? by lazy {
    initIndexes(indexesJsFileName)
  }

  fun hasIndexes(isJs: Boolean) = if (isJs) jsIndexes != null else jvmIndexes != null

  fun getClassesByName(name: String, isJs: Boolean): List<ImportInfo>? {
    val indexes = if (isJs) jsIndexes else jvmIndexes
    return indexes?.get(name)
  }

  private fun initIndexes(fileName: String): Map<String, List<ImportInfo>>? {
    val file = File(fileName)
    if (file.exists().not()) {
      log.warn("No file was found at path: $fileName")
      return null
    }
    val indexes = runCatching { readIndexesFromJson(file) }.getOrNull()
    if (indexes == null) {
      log.warn("Can not parse file=$fileName with indexes")
    }
    return indexes
  }

  private fun readIndexesFromJson(file: File): Map<String, List<ImportInfo>> =
    jacksonObjectMapper().readValue(file.readText())
}