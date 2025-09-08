import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  `java-library`
  id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
  id("com.gradleup.shadow") version "9.0.2"
}

group = "com.solarrabbit"
version = "1.11.7"
description = "LargeRaids"
val mcVersion = "1.21.4"

java {
  // Configure the java toolchain. This allows gradle to auto-provision JDK 21 on systems that only have JDK 8 installed for example.
  toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
  maven("https://repo.helpch.at/releases/")
  maven("https://mvn.lumine.io/repository/maven-public/")
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("")
  configurations = listOf(project.configurations.shadow.get())
  relocate("org.bstats", "com.solarrabbit.largeraids.bstats")
}

dependencies {
  paperweight.paperDevBundle(mcVersion + "-R0.1-SNAPSHOT")
  shadow("org.bstats:bstats-bukkit:3.1.0")
  implementation("me.clip:placeholderapi:2.11.6")
  implementation("io.lumine:Mythic-Dist:5.9.5")
}

tasks {
  // Use shadowJar as the main output
  jar {
    enabled = false
  }

  // Configure reobfJar to run when invoking the build task
  assemble {
    dependsOn(reobfJar)
  }

  compileJava {
    options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

    // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
    // See https://openjdk.java.net/jeps/247 for more information.
    options.release.set(21)
  }
  javadoc {
    options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
  }
  processResources {
    filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    expand("version" to version)
  }

  /*
  reobfJar {
    // This is an example of how you might change the output location for reobfJar. It's recommended not to do this
    // for a variety of reasons, however it's asked frequently enough that an example of how to do it is included here.
    outputJar.set(layout.buildDirectory.file("libs/PaperweightTestPlugin-${project.version}.jar"))
  }
   */
}

tasks.register("getVersion") {
    doLast {
        println(version)
    }
}

tasks.register("getMCVersion") {
    doLast {
        println(mcVersion)
    }
}
