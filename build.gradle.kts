import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.targets.js.KotlinJsCompilerAttribute
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion: String by System.getProperties()
val kotlinIdeVersion: String by System.getProperties()
val kotlinIdeVersionSuffix: String by System.getProperties()
val policy: String by System.getProperties()
val indexes: String by System.getProperties()
val indexesJs: String by System.getProperties()

group = "com.compiler.server"
version = "$kotlinVersion-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

val kotlinDependency: Configuration by configurations.creating {
    isTransitive = false
}
val kotlinJsDependency: Configuration by configurations.creating {
    isTransitive = false
    attributes {
        attribute(
            KotlinPlatformType.attribute,
            KotlinPlatformType.js
        )
        attribute(
            KotlinJsCompilerAttribute.jsCompilerAttribute,
            KotlinJsCompilerAttribute.ir
        )
    }
}

val kotlinWasmDependency: Configuration by configurations.creating {
    isTransitive = false
    attributes {
        attribute(
            KotlinPlatformType.attribute,
            KotlinPlatformType.wasm
        )
    }
}

val libJSFolder = "$kotlinVersion-js"
val libWasmFolder = "$kotlinVersion-wasm"
val libJVMFolder = kotlinVersion
val propertyFile = "application.properties"
val jacksonVersionKotlinDependencyJar = "2.14.0" // don't forget to update version in `executor.policy` file.

val copyDependencies by tasks.creating(Copy::class) {
    from(kotlinDependency)
    into(libJVMFolder)
}
val copyJSDependencies by tasks.creating(Copy::class) {
    from(files(Callable { kotlinJsDependency.map { zipTree(it) } }))
    into(libJSFolder)
}

val copyWasmDependencies by tasks.creating(Copy::class) {
    from(files(Callable { kotlinWasmDependency.map { zipTree(it) } }))
    into(libWasmFolder)
}

plugins {
    val kotlinVersion by System.getProperties()
    kotlin("jvm") version "$kotlinVersion"
    id("org.jetbrains.kotlin.kapt") version "$kotlinVersion"
    id("org.jetbrains.kotlin.plugin.allopen") version "$kotlinVersion"
    id("io.micronaut.application") version "4.0.2"
    id("io.micronaut.aot") version "4.0.2"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

apply<NodeJsRootPlugin>()

the<NodeJsRootExtension>().nodeVersion = "20.2.0"

allprojects {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-ide")
        maven("https://cache-redirector.jetbrains.com/jetbrains.bintray.com/intellij-third-party-dependencies")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-ide-plugin-dependencies")
        maven("https://www.myget.org/F/rd-snapshots/maven/")
        maven("https://kotlin.jetbrains.space/p/kotlin/packages/maven/kotlin-ide")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
    }
    afterEvaluate {
        dependencies {
            dependencies {
                implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
                implementation("org.jetbrains.kotlin:idea:231-$kotlinIdeVersion-$kotlinIdeVersionSuffix") {
                    isTransitive = false
                }
            }
        }
    }
}

dependencies {
    kotlinDependency("junit:junit:4.13.2")
    kotlinDependency("org.hamcrest:hamcrest:2.2")
    kotlinDependency("com.fasterxml.jackson.core:jackson-databind:$jacksonVersionKotlinDependencyJar")
    kotlinDependency("com.fasterxml.jackson.core:jackson-core:$jacksonVersionKotlinDependencyJar")
    kotlinDependency("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersionKotlinDependencyJar")
    // Kotlin libraries
    kotlinDependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    kotlinDependency("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
    kotlinDependency("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    kotlinDependency("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    kotlinDependency("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3")
    kotlinJsDependency("org.jetbrains.kotlin:kotlin-stdlib-js:$kotlinVersion")
    kotlinWasmDependency("org.jetbrains.kotlin:kotlin-stdlib-wasm:$kotlinVersion")

    // https://mvnrepository.com/artifact/io.micronaut/micronaut-core
    implementation("io.micronaut:micronaut-runtime")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    compileOnly("org.graalvm.nativeimage:svm")
    kapt("io.micronaut.serde:micronaut-serde-processor")

    implementation("com.google.code.gson:gson:2.8.5")
    implementation("junit:junit:4.13.2")
    implementation("net.logstash.logback:logstash-logback-encoder:7.3")
    runtimeOnly("ch.qos.logback:logback-classic")
    implementation("org.jetbrains.intellij.deps:trove4j:1.0.20221201")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-compiler:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-script-runtime:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-js:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-compiler-for-ide:$kotlinIdeVersion"){
        isTransitive = false
    }
    implementation("org.jetbrains.kotlin:core:231-$kotlinIdeVersion-$kotlinIdeVersionSuffix")
    implementation(project(":executors", configuration = "default"))
    implementation(project(":common", configuration = "default"))

    testImplementation("io.micronaut:micronaut-http-client")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}

application {
    mainClass.set("com.compiler.server.CompilerApplicationKt")
}

kotlin {
    jvmToolchain(17)
}


//ksp {
//    java {
//        arg("dagger.multibindings", "error")
//    }
//}

graalvmNative.toolchainDetection = false
micronaut {
    version = "4.0.2"
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.compiler.server.*")
    }
    aot {
        // Please review carefully the optimizations enabled below
        // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
    }
}

fun buildPropertyFile() {
    rootDir.resolve("src/main/resources/${propertyFile}").apply {
        println("Generate properties into $absolutePath")
        parentFile.mkdirs()
        writeText(generateProperties())
    }
}

fun generateProperties(prefix: String = "") = """
    # this file is autogenerated by build.gradle.kts
    kotlin.version=${kotlinVersion}
    policy.file=${prefix + policy}
    indexes.file=${prefix + indexes}
    indexesJs.file=${prefix + indexesJs}
    libraries.folder.jvm=${prefix + libJVMFolder}
    libraries.folder.js=${prefix + libJSFolder}
    libraries.folder.wasm=${prefix + libWasmFolder}
    spring.mvc.pathmatch.matching-strategy=ant_path_matcher
    server.compression.enabled=true
    server.compression.mime-types=application/json
""".trimIndent()

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion.set(JavaLanguageVersion.of("17"))
    }
}

tasks.named("inspectRuntimeClasspath") {
    dependsOn(":copyDependencies")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
    dependsOn(copyDependencies)
    dependsOn(copyJSDependencies)
    dependsOn(copyWasmDependencies)
    dependsOn(":executors:jar")
    dependsOn(":indexation:run")
    buildPropertyFile()
}

//tasks.withType<BootJar> {
//    requiresUnpack("**/kotlin-*.jar")
//    requiresUnpack("**/kotlinx-*.jar")
//}

val buildLambda by tasks.creating(Zip::class) {
    val lambdaWorkDirectoryPath = "/var/task/"
    from(tasks.compileKotlin)
    from(tasks.processResources) {
        eachFile {
            if (name == propertyFile) { file.writeText(generateProperties(lambdaWorkDirectoryPath)) }
        }
    }
    from(policy)
    from(indexes)
    from(indexesJs)
    from(libJSFolder) { into(libJSFolder) }
    from(libWasmFolder) { into(libWasmFolder) }
    from(libJVMFolder) { into(libJVMFolder) }
    into("lib") {
        from(configurations.compileClasspath) { exclude("tomcat-embed-*") }
    }
}

tasks.withType<Test> {
    dependsOn(rootProject.the<NodeJsRootExtension>().nodeJsSetupTaskProvider)
    useJUnitPlatform()
    doFirst {
        this@withType.environment(
            "kotlin.wasm.node.path",
            rootProject.the<NodeJsRootExtension>().requireConfigured().nodeExecutable
        )
    }
}
