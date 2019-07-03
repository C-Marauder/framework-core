//package com.androidx.http.processor
//
//import com.androidx.annotation.ApiService
//import com.androidx.annotation.Service
//import com.google.auto.service.AutoService
//import com.squareup.kotlinpoet.*
//import java.io.File
//import java.util.concurrent.ConcurrentHashMap
//import javax.annotation.processing.*
//import javax.lang.model.SourceVersion
//import javax.lang.model.element.ExecutableElement
//import javax.lang.model.element.TypeElement
//import javax.lang.model.type.DeclaredType
//import javax.lang.model.util.Elements
//import javax.tools.Diagnostic
//
///**
// *@desc
// *@creator 小灰灰
// *@Time 2019-06-27 - 13:56
// **/
//
//@AutoService(Processor::class)
//class ApiProcessor : AbstractProcessor() {
//    companion object {
//        private const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
//        private const val GENERATED_PACKAGE_NAME = "com.androidx.service"
//        private const val GENERATED_SERVICE_FILE_NAME = "AppService"
//
//    }
//
//    private lateinit var messager: Messager
//    private lateinit var elementUtils: Elements
//    override fun init(p0: ProcessingEnvironment?) {
//        super.init(p0)
//        messager = processingEnv.messager
//        elementUtils = processingEnv.elementUtils
//    }
//
//    override fun getSupportedAnnotationTypes(): MutableSet<String> {
//        val mutableSet = mutableSetOf<String>()
//        mutableSet.add(ApiService::class.java.name)
//        mutableSet.add(Service::class.java.name)
//        return mutableSet
//
//    }
//
//    override fun getSupportedSourceVersion(): SourceVersion {
//        return SourceVersion.latestSupported()
//    }
//
//    override fun process(p0: MutableSet<out TypeElement>?, p1: RoundEnvironment?): Boolean {
//        if (p1 == null) {
//            return false
//        }
////        var serviceMangerTypeSpec: TypeSpec.Builder? = null
////        var serviceMangerClzName: String? = null
//        //if (serviceManagerElements.isNullOrEmpty()) return false
//        messager.printMessage(Diagnostic.Kind.WARNING,"${serviceManagerElements.size}")
//
//        serviceManagerElements.forEach { _ ->
//            //            packageName = elementUtils.getPackageOf(serviceManager).qualifiedName.toString()
////            serviceMangerClzName = serviceManager.simpleName.toString()
//            messager.printMessage(Diagnostic.Kind.WARNING,"===>>>")
////            serviceMangerTypeSpec = TypeSpec.classBuilder(serviceMangerClzName!!)
////                .addKdoc("val mServiceMap:ConcurrentHashMap<String,AbsRepository> by lazy {" +
////                        "ConcurrentHashMap<String,AbsRepository>()"+
////                        "}")
//
////            messager.printMessage(Diagnostic.Kind.WARNING,it.simpleName.toString())
//        }
////        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]!!
////        val serviceMangerFile = FileSpec.builder(GENERATED_PACKAGE_NAME, "ServiceManager")
////            .build()
////        serviceMangerFile.writeTo(File(kaptKotlinGeneratedDir))
//        val serviceElements = p1.getElementsAnnotatedWith(ApiService::class.java)
//        if (serviceElements.isNullOrEmpty()) return false
//        val apiService = TypeSpec.interfaceBuilder(GENERATED_SERVICE_FILE_NAME)
//        serviceElements.forEach { serviceElement ->
//            if (serviceElement.kind.isInterface) {
//                val typeElement = serviceElement as TypeElement
//                typeElement.interfaces.forEach { typeMirror ->
//                    (typeMirror as DeclaredType).asElement().enclosedElements.forEach {
//                        createMethod(apiService, it as ExecutableElement)
//
//                    }
//                }
//                serviceElement.enclosedElements.forEach { service ->
//                    createMethod(apiService, service as ExecutableElement)
//
//                }
//            }
//
//        }
//        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]!!
//
//        val file = FileSpec.builder(GENERATED_PACKAGE_NAME, GENERATED_SERVICE_FILE_NAME)
//            .apply {
//                apiService.let {
//                    addType(apiService.build())
//                }
//            }.build()
//
//        file.writeTo(File(kaptKotlinGeneratedDir))
//        return true
//    }
//
//    private fun createMethod(builder: TypeSpec.Builder, executableElement: ExecutableElement?) {
//        executableElement?.let {
//            with(FunSpec.builder(it.simpleName.toString())) {
//                it.annotationMirrors.forEach {
//                    addAnnotation(AnnotationSpec.get(it))
//                }
//                addModifiers(KModifier.ABSTRACT)
//                returns(it.returnType.asTypeName())
//                builder.addFunction(build())
//            }
//        }
//
//    }
//}