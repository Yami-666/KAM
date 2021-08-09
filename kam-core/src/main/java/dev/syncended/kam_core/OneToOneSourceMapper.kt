package dev.syncended.kam_core

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class OneToOneSourceMapper(
    val toClass: KClass<*>
)