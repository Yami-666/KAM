package dev.syncended.kam_processor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import dev.syncended.kam_core.OneToOneSourceMapper
import java.util.stream.Collectors
import javax.annotation.processing.*
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic
import kotlin.properties.Delegates
import kotlin.reflect.KClass
import kotlin.streams.toList

@AutoService(Processor::class)
class KamProcessor : AbstractProcessor() {
    private var messager: Messager by Delegates.notNull()
    private var typeUtils: Types by Delegates.notNull()
    private var elementUtils: Elements by Delegates.notNull()

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(OneToOneSourceMapper::class.java.name)
    }

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        messager = processingEnv.messager
        typeUtils = processingEnv.typeUtils
        elementUtils = processingEnv.elementUtils
    }

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        messager.printMessage(Diagnostic.Kind.NOTE, "Running KAM processor")
        val oneToOneSourceAnnotations =
            roundEnv.getElementsAnnotatedWith(OneToOneSourceMapper::class.java)
        handleOneToOneSourceAnnotation(oneToOneSourceAnnotations)
        return false
    }

    private fun handleOneToOneSourceAnnotation(elements: Set<Element>) {
        val fileBuilder = FileSpec.builder(PROCESSOR_PACKAGE, ONE_TO_ONE_SOURCES_FILE)
        elements.forEach { element ->
            val toPackAndClass = element.getAnnotationClassValue<OneToOneSourceMapper> { toClass }
            val (pack, clazz) = toPackAndClass.getPackAndClass()
            val typeElement = elementUtils.getTypeElement(element.asType().toString())
            val fields = typeElement.enclosedElements
                .stream()
                .filter { o: Element -> o.kind.isField }
                .toList()

            val elPack = elementUtils.getPackageOf(element).toString()
            val elClass = element.simpleName.toString()

            val targetClass = ClassName(pack, clazz)

            val returnStatement = buildString {
                append("return %T(\n")
                fields.forEachIndexed { index, element ->
                    append("\t")
                    append(element)
                    append(" = ")
                    append(element)
                    if (index != fields.lastIndex) {
                        append(",")
                    }
                    append("\n")
                }
                append(")")
            }
            val function = FunSpec.builder("mapTo$clazz")
                .receiver(ClassName(elPack, elClass))
                .returns(targetClass)
                .addStatement(returnStatement, targetClass)
                .build()
            fileBuilder.addFunction(function)
        }
        fileBuilder.build().writeTo(System.out)
    }

    private fun String.getPackAndClass(): Pair<String, String> {
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

    companion object {
        private const val PROCESSOR_PACKAGE = "dev.syncended.mapper"
        private const val ONE_TO_ONE_SOURCES_FILE = "OneToOneSources"
    }
}