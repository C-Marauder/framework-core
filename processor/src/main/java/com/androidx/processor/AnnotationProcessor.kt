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
import com.androidx.annotation.Service
import com.androidx.annotation.Contract
import com.androidx.annotation.ApiService
import com.androidx.annotation.Module
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypesException
import javax.tools.Diagnostic


@AutoService(Processor::class)
class AnnotationProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        private const val GENERATED_PACKAGE_NAME = "com.xqy.androidx.framework"
        private const val GENERATED_CLASS_NAME = "FrameworkManager"
        private const val MVI_PACKAGE = "com.androidx.frameworkcore.logic"
        private const val VIEW_CLASS_NAME = "AppView"
        private const val PRESENTER_CLASS_NAME = "AppPresenter"
        private const val REPOSITORY_CLASS_NAME = "AbsRepository"
        private const val REPOSITORY_PACKAGE_NAME = "com.xqy.androidx.framework"
        private const val GENERATED_SERVICE_FILE_NAME = "AndroidService"
    }

    private lateinit var pkgName: String
    private lateinit var messager: Messager
    private lateinit var elementUtils: Elements
    override fun init(p0: ProcessingEnvironment?) {
        super.init(p0)
        messager = processingEnv.messager
        elementUtils = processingEnv.elementUtils
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        val mutableSet = mutableSetOf<String>()
        mutableSet.add(Module::class.java.name)
        mutableSet.add(Service::class.java.name)
        mutableSet.add(Contract::class.java.name)
        mutableSet.add(ApiService::class.java.name)
        return mutableSet

    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun process(p0: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment?): Boolean {
        if (roundEnvironment == null) {
            return false
        }
        val mModuleElement = roundEnvironment.getElementsAnnotatedWith(Module::class.java)
        val mRepositoryElement = roundEnvironment.getElementsAnnotatedWith(Service::class.java)
        val mContractElement = roundEnvironment.getElementsAnnotatedWith(Contract::class.java)
        val mApiServiceElement = roundEnvironment.getElementsAnnotatedWith(ApiService::class.java)
        var moduleElement: Element? = null
        if (!mModuleElement.isNullOrEmpty()) {
            moduleElement = mModuleElement.first()

        }
        pkgName = if (moduleElement == null) {
            GENERATED_PACKAGE_NAME
        } else {
            GENERATED_PACKAGE_NAME + "."+moduleElement.simpleName
        }

        if (mRepositoryElement.isNullOrEmpty()) return false
        val string = ClassName("kotlin", "String")
        //val any = ClassName("kotlin", "Any")
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
                                TypeVariableName("*")
                            )
                        )
                    )
                )
        ).build()
        val constructorBuilder = FunSpec.constructorBuilder().addParameter(mRepositoryHashMap)
        val mView = ClassName(MVI_PACKAGE, VIEW_CLASS_NAME)
        mRepositoryElement.forEach {
            val key = it.getAnnotation(Service::class.java).name
            val packageName = elementUtils.getPackageOf(it).qualifiedName
            constructorBuilder.addCode("mRepositoryHashMap[\"$key\"]= $packageName.${it.simpleName}::class\n")
                .build()
        }
        val mKClass = ClassName("kotlin.reflect", "KClass")
        val mPresenter =
            ClassName(MVI_PACKAGE, PRESENTER_CLASS_NAME).parameterizedBy(WildcardTypeName.producerOf(mView))
        val mPresenterListHashMap = ParameterSpec.builder(
            "mPresenterListHashMap",
            ConcurrentHashMap::class.asTypeName().parameterizedBy(
                mKClass.parameterizedBy(WildcardTypeName.producerOf(mPresenter)),
                string
            )
        ).build()
        constructorBuilder.addParameter(mPresenterListHashMap)
        if (mContractElement.isNullOrEmpty()) return false
        mContractElement.forEach { contractElement ->
            val packageName = elementUtils.getPackageOf(contractElement).qualifiedName.toString().trim()

            val names = contractElement.getAnnotation(Contract::class.java).names.forEach { ui ->
                contractElement.enclosedElements.forEachIndexed { _, element ->
                    if (element.kind.isClass) {
                        val mPresenterName = element.simpleName.toString().trim()
                        constructorBuilder.addCode("mPresenterListHashMap[$packageName.${contractElement.simpleName.toString().trim()}.$mPresenterName::class]= \"${ui}\"\n")
                    }
                }
            }

        }

//        if (mApiServiceElement.isNullOrEmpty()) return false
        val apiService = TypeSpec.interfaceBuilder(GENERATED_SERVICE_FILE_NAME)
        mApiServiceElement.forEach { serviceElement ->
            if (serviceElement.kind.isInterface) {
                val typeElement = serviceElement as TypeElement
                typeElement.interfaces.forEach { typeMirror ->
                    (typeMirror as DeclaredType).asElement().enclosedElements.forEach { service ->
                        createMethod(apiService, service as ExecutableElement)

                    }
                }
                serviceElement.enclosedElements.forEach { service ->
                    createMethod(apiService, service as ExecutableElement)

                }
            }
        }
        val apiServiceFile = FileSpec.builder("$GENERATED_PACKAGE_NAME.service", GENERATED_SERVICE_FILE_NAME)
            .addType(apiService.build()).build()
        val file = FileSpec.builder(pkgName, GENERATED_CLASS_NAME).addType(
            TypeSpec.classBuilder(GENERATED_CLASS_NAME)
                .primaryConstructor(constructorBuilder.build())
                .build()
        )
            .build()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]!!
        file.writeTo(File(kaptKotlinGeneratedDir))
        apiServiceFile.writeTo(File(kaptKotlinGeneratedDir))
        return true

    }

    private fun createMethod(builder: TypeSpec.Builder, executableElement: ExecutableElement?) {
        executableElement?.let {
            with(FunSpec.builder(it.simpleName.toString())) {
                it.annotationMirrors.forEach {
                    addAnnotation(AnnotationSpec.get(it))
                }
                addModifiers(KModifier.ABSTRACT)
                returns(it.returnType.asTypeName())
                builder.addFunction(build())
            }
        }

    }

}
