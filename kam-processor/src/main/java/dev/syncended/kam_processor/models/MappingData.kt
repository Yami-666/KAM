package dev.syncended.kam_processor.models

data class MappingData(
    val fromPackage: String,
    val fromClass: String,
    val toPackage: String,
    val toClass: String,
    val fields: List<FieldInfo>
)