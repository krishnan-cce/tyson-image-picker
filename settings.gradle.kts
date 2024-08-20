pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven {url = uri("https://jitpack.io") }
//        maven {
//            name = "GitHubPackages"
//            url = uri("https://maven.pkg.github.com/krishnan-cce/tyson-image-picker")
//            credentials {
//                username = System.getenv("GIT_USERNAME")
//                password = System.getenv("GIT_PASSWORD")
//            }
//        }
    }
}

rootProject.name = "ComposeCropper"
include(":app")
include(":libs:imagePicker")

