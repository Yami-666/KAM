package dev.syncended.kam_project

import dev.syncended.kam_core.OneToOneMapperSource

@OneToOneMapperSource(
    toClass = Model2::class
)
data class Model(
    val id: String
)

data class Model2(
    val id: String
)