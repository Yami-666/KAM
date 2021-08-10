package dev.syncended.kam_processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import dev.syncended.kam_processor.models.MappingData
import javax.annotation.processing.Messager
import javax.tools.Diagnostic

class CodeWriter(private val messager: Messager) {
    fun buildCode(data: List<MappingData>) {
        messager.printMessage(Diagnostic.Kind.NOTE, "Start code writing")
        val fileSpec = FileSpec.builder(PACKAGE, FILE_NAME)

        data.forEach {
            val func = generateFun(it)
            fileSpec.addFunction(func)
        }
        fileSpec.build()
            .writeTo(System.out)

        messager.printMessage(Diagnostic.Kind.NOTE, "Code writing finished")
    }

    private fun generateFun(data: MappingData): FunSpec {
        val funName = "$FUN_NAME_PREFIX${data.toClass}"
        val resultClass = ClassName(data.toPackage, data.toClass)

        val mapperBody = buildString {
            append(RETURN_STATEMENT)
            data.fields.forEachIndexed { index, field ->
                append(TAB)

                append(field.variableName)
                append(EQUALS_STATEMENT)
                append(field.variableName)

                if (index != data.fields.lastIndex) {
                    append(NEXT_VARIABLE)
                } else {
                    append(NEW_LINE)
                }
            }
            append(END_BRACKET)
        }

        return FunSpec.builder(funName)
            .receiver(ClassName(data.fromPackage, data.fromClass))
            .returns(resultClass)
            .addStatement(mapperBody, resultClass)
            .build()
    }

    companion object {
        private const val NEW_LINE = "\n"
        private const val RETURN_STATEMENT = "return %T($NEW_LINE"
        private const val TAB = "    "
        private const val EQUALS_STATEMENT = " = "
        private const val NEXT_VARIABLE = ",$NEW_LINE"
        private const val END_BRACKET = ")"
        private const val FUN_NAME_PREFIX = "mapTo"
        private const val FILE_NAME = "Mappers"
        private const val PACKAGE = "dev.syncended.kam_generated"
    }
}