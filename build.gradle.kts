import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation

// Template by Nxllpointer, thanks ❤
// https://github.com/Nxllpointer/AdventOfCode2022/blob/9beb193f826f9110ebe436645f3176bf6da5869a/build.gradle.kts

plugins {
    kotlin("jvm") version "2.0.0-Beta1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

val currentYear = 2024
val currentDay = 6

repositories {
    mavenCentral()
    mavenLocal()
}

kotlin {
    target {
        compilations {
            val common by creating

            for (day in 1..currentDay) {
                val dayCompilation = create("${currentYear}_day_$day") {
                    associateWith(common)
                    generateAOCBaseStructure(currentYear, day)

                    dependencies {
                        implementation("io.github.zabuzard.maglev:maglev:1.2")
                    }
                }

                tasks.create<JavaExec>("run${currentYear}Day$day") {
                    group = "aoc"
                    mainClass.set("${currentYear}Day${day}Kt")
                    classpath = dayCompilation.runtimeDependencyFiles
                }
            }

            all {
                kotlinOptions {
                    jvmTarget = "21"
                }
            }
        }
    }

    jvmToolchain(21)
}

fun KotlinCompilation<*>.generateAOCBaseStructure(year: Int, day: Int) {
    val kotlinDir = defaultSourceSet.kotlin.sourceDirectories.first { it.name == "kotlin" }
    val resourcesDir = defaultSourceSet.resources.srcDirs.first()
    val mainFile = kotlinDir.resolve("${year}Day${day}.kt")
    val inputFile = resourcesDir.resolve("input.txt")
    kotlinDir.mkdirs()
    resourcesDir.mkdirs()
    inputFile.createNewFile()

    if (mainFile.createNewFile()) {
        val templateContent = """
            // AOC Year $year Day $day
            fun main() {
                val lines = {}::class.java.getResourceAsStream("input.txt")!!.bufferedReader().readLines()
                
                
            }
        """.trimIndent()

        mainFile.writeText(templateContent)
    }
}
