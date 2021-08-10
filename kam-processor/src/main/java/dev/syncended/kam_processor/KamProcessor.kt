package dev.syncended.kam_processor

import com.google.auto.service.AutoService
import dev.syncended.kam_core.OneToOneMapperSource
import javax.annotation.processing.*
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
class KamProcessor : AbstractProcessor() {
    private lateinit var messager: Messager
    private lateinit var mappingBuilder: MappingDataBuilder
    private lateinit var codeWriter: CodeWriter

    override fun getSupportedAnnotationTypes(): MutableSet<String> = mutableSetOf(
        OneToOneMapperSource::class.java.name
    )

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        messager = processingEnv.messager
        mappingBuilder = MappingDataBuilder(
            messager = messager,
            typeUtils = processingEnv.typeUtils,
            elementUtils = processingEnv.elementUtils
        )
        codeWriter = CodeWriter(messager)
    }

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        messager.printMessage(Diagnostic.Kind.NOTE, "Running KAM processor")
        val mappingData = mappingBuilder.buildMappingData(roundEnv)
        codeWriter.buildCode(mappingData)
        return false
    }
}