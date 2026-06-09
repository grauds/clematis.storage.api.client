rootProject.name = "clematis.storage.api.client"

include("clematis.storage.api.client-spring2")

// Set the directory using the type-safe project reference syntax
project(":clematis.storage.api.client-spring2").projectDir = file("spring2")