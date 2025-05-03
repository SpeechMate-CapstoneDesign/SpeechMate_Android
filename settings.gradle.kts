pluginManagement {
    includeBuild("build-logic")
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
        maven { url = java.net.URI("https://devrepo.kakao.com/nexus/content/groups/public/") }

    }
}

rootProject.name = "speechmate"

include(":app")

include(":feature")
include(":feature:main")


include(":core")
include(":core:designsystem")
include(":core:data")
include(":core:domain")
include(":core:common")
include(":core:network")
include(":core:navigation")
include(":feature:practice")
