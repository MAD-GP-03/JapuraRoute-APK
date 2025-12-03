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
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication {
                create<BasicAuthentication>("basic")
            }
            credentials {
                username = "nuwankonara"
                password = providers.gradleProperty("sk.eyJ1IjoibnV3YW5rb25hcmEiLCJhIjoiY21paXA3amZ0MGo0cjNlcjFxZjlmeHlnMyJ9.-IHmboDlEFRSlL7eQzsTIg")
                    .getOrElse(providers.environmentVariable("sk.eyJ1IjoibnV3YW5rb25hcmEiLCJhIjoiY21paXA3amZ0MGo0cjNlcjFxZjlmeHlnMyJ9.-IHmboDlEFRSlL7eQzsTIg").getOrElse(""))
            }
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "JapuraRouteF"
include(":app")
 