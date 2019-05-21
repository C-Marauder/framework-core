package com.androidx.processor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic
import kotlin.reflect.KClass


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Service(val name: String)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Presenter

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Contract(val uiNames: Array<String>)

@AutoService(Processor::class)
class AnnotationProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        private const val GENERATED_PACKAGE_NAME = "com.androidx.frameworkcore"
        private const val GENERATED_CLASS_NAME = "FrameworkManager"
        private const val MVI_PACKAGE = "com.androidx.frameworkcore.mvi"
        private const val VIEW_CLASS_NAME = "AppView"
        private const val PRESENTER_CLASS_NAME = "AppPresenter"
        private const val REPOSITORY_CLASS_NAME = "AbsRepository"
        private const val REPOSITORY_PACKAGE_NAME = "com.androidx.frameworkcore.viewmodel.repository"
    }

    private lateinit var messager: Messager
    private lateinit var elementUtils: Elements
    override fun init(p0: ProcessingEnvironment?) {
        super.init(p0)
        messager = processingEnv.messager
        elementUtils = processingEnv.elementUtils
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        val mutableSet = mutableSetOf<String>()
        mutableSet.add(Service::class.java.name)
        mutableSet.add(Presenter::class.java.name)
        mutableSet.add(Contract::class.java.name)
        return mutableSet

    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun process(p0: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment?): Boolean {
        if (roundEnvironment == null) {
            return false
        }
        val mRepositoryElement = roundEnvironment.getElementsAnnotatedWith(Service::class.java)
        val mContractElement = roundEnvironment.getElementsAnnotatedWith(Contract::class.java)
        if (mRepositoryElement.isNullOrEmpty()) return false
        val string = ClassName("kotlin", "String")
        val any = ClassName("kotlin", "Any")
        val kRepositoryClass = ClassName("kotlin.reflect", "KClass")
        val mRepository = ClassName(REPOSITORY_PACKAGE_NAME, REPOSITORY_CLASS_NAME)
        val mRepositoryHashMap = ParameterSpec.builder(
            "mRepositoryHashMap", ConcurrentHashMap::class.asTypeName()
                .parameterizedBy(
                    string,
                    kRepositoryClass.parameterizedBy(
                        WildcardTypeName.producerOf(
                            mRepository.parameterizedBy(
                                TypeVariableName("*"),
                                TypeVariableName("*"),
                                TypeVariableName("*")
                            )
                        )
                    )
                )
        ).build()
        val mView = ClassName(MVI_PACKAGE, VIEW_CLASS_NAME)
        val funSpecBuilder = FunSpec.builder("initFramework")
            .addParameter(mRepositoryHashMap)
        mRepositoryElement.forEach {
            val key = it.getAnnotation(Service::class.java).name
            val packageName = elementUtils.getPackageOf(it).qualifiedName
            funSpecBuilder.addCode("mRepositoryHashMap[\"$key\"]= $packageName.${it.simpleName}::class\n")
                .build()
        }
        val mKClass = ClassName("kotlin.reflect","KClass")
        val mPresenter =
            ClassName(MVI_PACKAGE, PRESENTER_CLASS_NAME).parameterizedBy(WildcardTypeName.producerOf(mView))
        val mPresenterListHashMap = ParameterSpec.builder(
            "mPresenterListHashMap",
            HashMap::class.asTypeName().parameterizedBy(mKClass.parameterizedBy(WildcardTypeName.producerOf(mPresenter)), string)
        ).build()
        funSpecBuilder.addParameter(mPresenterListHashMap)
        if (mContractElement.isNullOrEmpty()) return false
        mContractElement.forEach { contractElement ->
            val mNeedBindUINames = contractElement.getAnnotation(Contract::class.java).uiNames
            mNeedBindUINames.forEachIndexed { index, s ->
                val packageName = elementUtils.getPackageOf(contractElement).qualifiedName.toString().trim()
                contractElement.enclosedElements.forEach { childElement ->
                    if (childElement.kind.isClass) {
                        val mPresenterName = childElement.simpleName.toString().trim()
                        if (index == 0) {
                            funSpecBuilder.addCode("val m$mPresenterName = $packageName.${contractElement.simpleName.toString().trim()}.$mPresenterName::class\n")
                        }
                        funSpecBuilder.addCode("mPresenterListHashMap[m$mPresenterName]= \"$s\"\n")

                    }

                }
            }
        }

        val file = FileSpec.builder(GENERATED_PACKAGE_NAME, GENERATED_CLASS_NAME).addType(
            TypeSpec.classBuilder(GENERATED_CLASS_NAME)
                .addFunction(funSpecBuilder.build())
                .build()
        ).build()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir))
        return true

    }

}
