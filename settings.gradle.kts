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
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/krishnan-cce/kl_compose_image_picker")
            credentials {
                username ="krishnan-cce"
                password = "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"
            }
        }
    }
}

rootProject.name = "ComposeCropper"
include(":app")
include(":libs:imagePicker")

