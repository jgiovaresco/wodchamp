plugins {
  kotlin("jvm") version "1.5.31"
  kotlin("plugin.allopen") version "1.5.31"
  id("io.quarkus")
  id("com.diffplug.spotless") version "6.0.0"
}

repositories {
  mavenCentral()
  mavenLocal()
  maven {
    url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
  }
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
  val guavaVersion = "31.0.1-jre"

  val junitVersion = "5.8.1"
  val restAssuredVersion = "4.4.0"
  val fakerVersion = "1.10.0-SNAPSHOT"
  val striktVersion = "0.33.0"

  implementation(enforcedPlatform("$quarkusPlatformGroupId:$quarkusPlatformArtifactId:$quarkusPlatformVersion"))
  implementation("io.quarkus:quarkus-kotlin:$quarkusPlatformVersion")
  implementation("io.quarkus:quarkus-arc:$quarkusPlatformVersion")
  implementation("io.quarkus:quarkus-resteasy:$quarkusPlatformVersion")
  implementation("io.quarkus:quarkus-jacoco:$quarkusPlatformVersion")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  implementation("com.google.guava:guava:$guavaVersion")

  testImplementation("io.quarkus:quarkus-junit5:$quarkusPlatformVersion")
  testImplementation(platform("org.junit:junit-bom:$junitVersion"))
  testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
  testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")
  testImplementation("io.github.serpro69:kotlin-faker:$fakerVersion")
  testImplementation("io.strikt:strikt-core:$striktVersion")
}

group = "com.wodchamp"
version = "1.0-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_16
  targetCompatibility = JavaVersion.VERSION_16
}

allOpen {
  annotation("javax.ws.rs.Path")
  annotation("javax.enterprise.context.ApplicationScoped")
  annotation("io.quarkus.test.junit.QuarkusTest")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions.jvmTarget = JavaVersion.VERSION_16.toString()
  kotlinOptions.javaParameters = true
}

spotless {
  kotlin {
    ktfmt().googleStyle()
  }
}
