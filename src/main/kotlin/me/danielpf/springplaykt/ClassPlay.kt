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