package me.danielpf.springplaykt

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component


/*
Variance in kotlin
 - Declaration-site variance(using in and out)
 - Use-site variance: Type projection


Given 2 classes:
B for short Base, and D for short Derived, D extends B in Java, or D : B in kotlin,
we can say B > D, according to https://zh.wikipedia.org/wiki/%E9%87%8C%E6%B0%8F%E6%9B%BF%E6%8D%A2%E5%8E%9F%E5%88%99

and here a transition operation T,

Variance
 - Covariance -> T(B) > T(D)
 - Contravariance -> T(D) < T(B)
 - invariant -> no relationship between T(D) and T(B)

T<out E> hits Covariance
T(D) < T(B) < T<Any?> = T<*>, then return will be upper bond

T<in E> hits Contravariance
T(B) < T(D) < T<Nothing> = T<*>, then args will be Nothing

T<E> hits invariant
T<B> < T(*), return will be upper bond and arg will be Nothing
T<D> < T(*), return will be upper bond and arg will be Nothing


reference:
https://kotlinlang.org/docs/generics.html#star-projections
https://www.jianshu.com/p/b5ebee051541
https://www.geeksforgeeks.org/kotlin-generics/


Output for below code
starIns: 3
starOuts: 3
starNoInNoOut: 3
anyIns: 0
anyOuts: 3
anyNoInNoOut: 0
nothingIns: 3
nothingOuts: 3
nothingNoInNoOut: 3
numberInIns: 1
intInIns: 2
numberInNoInNoOut: 1
numberOutOuts: 2
intOutIns: 1
numberOutNoInNoOut: 2
* */

interface In<in E>

@Component
class InNumber : In<Number>

@Component
class InInt : In<Int>

@Component
class InString : In<String>

interface Out<out E>

@Component
class OutNumber : Out<Number>

@Component
class OutInt : Out<Int>

@Component
class OutString : Out<String>


interface NoInNoOut<E>

@Component
class NoInNoOutNumber : NoInNoOut<Number>

@Component
class NoInNoOutInt : NoInNoOut<Int>

@Component
class NoInNoOutString : NoInNoOut<String>

@Component
class GenericsInOut(

    val starIns: List<In<*>>,
    val starOuts: List<Out<*>>,
    val starNoInNoOut: List<NoInNoOut<*>>,

    val anyIns: List<In<Any?>>,
    val anyOuts: List<Out<Any?>>,
    val anyNoInNoOut: List<NoInNoOut<Any?>>,

    val nothingIns: List<In<Nothing>>,
    val nothingOuts: List<Out<Nothing>>,
    val nothingNoInNoOut: List<NoInNoOut<Nothing>>,


    val numberInIns: List<In<in Number>>,
    //val intInOuts: List<Out<in Int>>, // can't compile, in/out conflicts
    val intInIns: List<In<in Int>>,
    val numberInNoInNoOut: List<NoInNoOut<in Number>>,

    // val numberOutIns: List<In<out Number>>, // can't compile, in/out conflicts
    val numberOutOuts: List<Out<out Number>>,
    val intOutIns: List<Out<out Int>>,
    val numberOutNoInNoOut: List<NoInNoOut<out Number>>

) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {

        println("starIns: ${starIns.size}")
        println("starOuts: ${starOuts.size}")
        println("starNoInNoOut: ${starNoInNoOut.size}")

        println("anyIns: ${anyIns.size}")
        println("anyOuts: ${anyOuts.size}")
        println("anyNoInNoOut: ${anyNoInNoOut.size}")

        println("nothingIns: ${nothingIns.size}")
        println("nothingOuts: ${nothingOuts.size}")
        println("nothingNoInNoOut: ${nothingNoInNoOut.size}")

        println("numberInIns: ${numberInIns.size}")
        println("intInIns: ${intInIns.size}")
        println("numberInNoInNoOut: ${numberInNoInNoOut.size}")

        println("numberOutOuts: ${numberOutOuts.size}")
        println("intOutIns: ${intOutIns.size}")
        println("numberOutNoInNoOut: ${numberOutNoInNoOut.size}")

    }
}