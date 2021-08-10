package dev.syncended.kam_processor

import dev.syncended.kam_core.OneToOneMapperSource
import dev.syncended.kam_processor.models.FieldInfo
import dev.syncended.kam_processor.models.MappingData
import javax.annotation.processing.Messager
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic
import kotlin.reflect.KClass

class MappingDataBuilder(
    private val messager: Messager,
    private val typeUtils: Types,
    private val elementUtils: Elements
) {
    fun buildMappingData(env: RoundEnvironment): List<MappingData> {
        messager.printMessage(Diagnostic.Kind.NOTE, "Collecting data from OneToOneMapperSource")
        val oneToOneSourceAnnotations = env.getElementsAnnotatedWith(
            OneToOneMapperSource::class.java
        )
        val oneToOneSourceData = parseOneToOneSource(oneToOneSourceAnnotations)
        return oneToOneSourceData
    }

    private fun parseOneToOneSource(elements: Set<Element>): List<MappingData> {
        return elements.map { element ->
            //Getting source class info
            val fromPack = elementUtils.getPackageOf(element).toString()
            val fromClass = element.simpleName.toString()

            //Getting target class info
            val toPackAngClass = element.getAnnotationClassValue<OneToOneMapperSource> { toClass }
            val (pack, clazz) = toPackAngClass.getPackageAndClass()

            //Getting source class fields
            val typeElement = elementUtils.getTypeElement(element.asType().toString())
            val fields = typeElement.enclosedElements
                .filter { o: Element -> o.kind.isField }
                .map {
                    FieldInfo(
                        variableName = it.toString(),
                        variableType = it.asType().toString()
                    )
                }

            MappingData(
                fromClass = fromClass,
                fromPackage = fromPack,
                toClass = clazz,
                toPackage = pack,
                fields = fields
            )
        }
    }

    private fun String.getPackageAndClass(): Pair<String, String> {
        val splitted = this.split(".")
        val className = splitted.last()
        val packName = splitted.subList(0, splitted.size - 1).joinToString(".")
        return packName to className
    }

    private inline fun <reified T : Annotation> Element.getAnnotationClassValue(f: T.() -> KClass<*>) =
        try {
            getAnnotation(T::class.java).f()
            throw Exception("Expected to get a MirroredTypeException")
        } catch (e: MirroredTypeException) {
            e.typeMirror
        }.toString()

}