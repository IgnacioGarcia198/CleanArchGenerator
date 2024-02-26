plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.jetbrains.intellij") version "1.13.3"
}

group = "com.garcia.ignacio.cleanarchgenerator"
version = "0.1.1"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.2.5")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("222")
        untilBuild.set("232.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

//    withType<Jar> {
//        // Otherwise you'll get a "No main manifest attribute" error
//        manifest {
//            attributes["Main-Class"] = "com.example.cleanarchgenerator.ui.CustomListWithInlineEditingKt"
//        }
//
//        // To avoid the duplicate handling strategy error
//        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
//
//        // To add all of the dependencies
//        from(sourceSets.main.get().output)
//
//        dependsOn(configurations.runtimeClasspath)
//        from({
//            configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
//        })
//    }
}

dependencies {
    //implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.22"))
    //implementation(":kotlin-stdlib-jdk8-1.9.22")
    // https://mvnrepository.com/artifact/org.swinglabs/swingx
    implementation ("org.swinglabs:swingx:1.6.1")
}

