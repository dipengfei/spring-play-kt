package me.danielpf.springplaykt

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.util.ClassUtils

private val topLevelLambda: () -> Unit = {}

private val topLevelObject = object {
    val name = "Top"
    val age = 10
}

@Component
class ClassPlay : ApplicationRunner {

    companion object CompanionObject

    private val propertyLambda: () -> Unit = {}

    private val propertyObject = object {
        val name = "Property"
        val age = 11
    }

    private fun printClass(name: String, value: Any) {
        println("class      \t$name\t${value.javaClass.name}")
        println("super-class\t$name\t${value.javaClass.superclass.name}")
        ClassUtils.getAllInterfaces(value).joinToString { it.name }.also { println("interfaces\t$name\t$it") }
    }

    private fun play(block: () -> Unit) = printClass("argLambda", block)

    override fun run(args: ApplicationArguments?) {
        val localLambda: () -> Unit = {}

        val localObject = object {
            val name = "Local"
            val age = 12
        }

        printClass("topLevelLambda", topLevelLambda)
        printClass("propertyLambda", propertyLambda)
        printClass("localLambda", localLambda)


        printClass("topLevelObject", topLevelObject)
        printClass("propertyObject", propertyObject)
        printClass("localObject", localObject)

        printClass("companionObject", CompanionObject)

    }

}

/*
*
class      	topLevelLambda	me.danielpf.springplaykt.ClassPlayKt$topLevelLambda$1
super-class	topLevelLambda	kotlin.jvm.internal.Lambda
interfaces	topLevelLambda	kotlin.jvm.functions.Function0, kotlin.jvm.internal.FunctionBase, java.io.Serializable
class      	propertyLambda	me.danielpf.springplaykt.ClassPlay$propertyLambda$1
super-class	propertyLambda	kotlin.jvm.internal.Lambda
interfaces	propertyLambda	kotlin.jvm.functions.Function0, kotlin.jvm.internal.FunctionBase, java.io.Serializable
class      	localLambda	me.danielpf.springplaykt.ClassPlay$run$localLambda$1
super-class	localLambda	kotlin.jvm.internal.Lambda
interfaces	localLambda	kotlin.jvm.functions.Function0, kotlin.jvm.internal.FunctionBase, java.io.Serializable
class      	topLevelObject	me.danielpf.springplaykt.ClassPlayKt$topLevelObject$1
super-class	topLevelObject	java.lang.Object
interfaces	topLevelObject
class      	propertyObject	me.danielpf.springplaykt.ClassPlay$propertyObject$1
super-class	propertyObject	java.lang.Object
interfaces	propertyObject
class      	localObject	me.danielpf.springplaykt.ClassPlay$run$localObject$1
super-class	localObject	java.lang.Object
interfaces	localObject
class      	companionObject	me.danielpf.springplaykt.ClassPlay$CompanionObject
super-class	companionObject	java.lang.Object
interfaces	companionObject
*
* */