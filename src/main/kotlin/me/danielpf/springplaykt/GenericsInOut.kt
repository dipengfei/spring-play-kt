package me.danielpf.springplaykt

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.ResolvableType
import org.springframework.stereotype.Component
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField


/*
Variance in kotlin
 - Declaration-site variance(using in and out)
 - Use-site variance: Type projection


Given 2 classes:
B for short Base, and D for short Derived, D extends B in Java, or D : B in kotlin,
we can say B > D, according to https://zh.wikipedia.org/wiki/%E9%87%8C%E6%B0%8F%E6%9B%BF%E6%8D%A2%E5%8E%9F%E5%88%99

and here a transition operation T,

Variance
 - Covariance -> T(B) > T(D) -> out
 - Contravariance -> T(D) < T(B) -> in
 - invariant -> no relationship between T(D) and T(B) -> no in no out

T<out E> hits Covariance
T(D) < T(B) < T<*> <= T<Any?>

T<in E> hits Contravariance
T(B) < T(D) < T<*> <= T<Nothing>

T<E> hits invariant
T<B> < T(*), out -> Any?, in -> Nothing
T<D> < T(*), out -> Any?, in -> Nothing

* and Nothing will be resolved as Raw Types

reference:
https://kotlinlang.org/docs/generics.html#star-projections
https://www.jianshu.com/p/b5ebee051541
https://www.geeksforgeeks.org/kotlin-generics/


Output for below code
property_to_inject: anyIns	resolved_type_to_match: List<In<Any?>>	matched_beans: 0
property_to_inject: anyNoInNoOut	resolved_type_to_match: List<NoInNoOut<Any?>>	matched_beans: 0
property_to_inject: anyOuts	resolved_type_to_match: List<Out<Any?>>	matched_beans: 3
property_to_inject: intInIns	resolved_type_to_match: List<In<Integer>>	matched_beans: 2
property_to_inject: intInNoInNoOut	resolved_type_to_match: List<NoInNoOut<Integer>>	matched_beans: 2
property_to_inject: intOutIns	resolved_type_to_match: List<Out<Integer>>	matched_beans: 1
property_to_inject: intOutNoInNoOut	resolved_type_to_match: List<NoInNoOut<Integer>>	matched_beans: 1
property_to_inject: nothingIns	resolved_type_to_match: List<In<>>	matched_beans: 3
property_to_inject: nothingNoInNoOut	resolved_type_to_match: List<NoInNoOut<>>	matched_beans: 3
property_to_inject: nothingOuts	resolved_type_to_match: List<Out<>>	matched_beans: 3
property_to_inject: numberInIns	resolved_type_to_match: List<In<Number>>	matched_beans: 1
property_to_inject: numberInNoInNoOut	resolved_type_to_match: List<NoInNoOut<Number>>	matched_beans: 1
property_to_inject: numberOutNoInNoOut	resolved_type_to_match: List<NoInNoOut<Number>>	matched_beans: 2
property_to_inject: numberOutOuts	resolved_type_to_match: List<Out<Number>>	matched_beans: 2
property_to_inject: starIns	resolved_type_to_match: List<In<>>	matched_beans: 3
property_to_inject: starNoInNoOut	resolved_type_to_match: List<NoInNoOut<>>	matched_beans: 3
property_to_inject: starOuts	resolved_type_to_match: List<Out<>>	matched_beans: 3
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

    /*
    * Output:
    * property_to_inject: starIns	resolved_type_to_match: List<In<>>	matched_beans: 3
    *
    * Explanation:
    * in hits Contravariance, but * will be treated as Raw type, In<*> = In
    * -> In > In<Int> > In<Number>
    * -> In > In<String>
    *
    * Conclusion:
    * 3 beans matched: In<Number>, In<Int>, In<String>
    * */
    val starIns: List<In<*>>,

    /*
    * Output:
    * property_to_inject: starOuts	resolved_type_to_match: List<Out<>>	matched_beans: 3
    *
    * Explanation:
    * out hits Covariance, but * will be treated as Raw type, Out<*> = Out
    * -> Out > Out<Number> > Out<Int>
    * -> Out > Out<String>
    *
    * Conclusion:
    * 3 beans matched: Out<Number>, Out<Int>, Out<String>
    * */
    val starOuts: List<Out<*>>,

    /*
    * Output:
    * property_to_inject: starNoInNoOut	resolved_type_to_match: List<NoInNoOut<>>	matched_beans: 3
    *
    * Explanation:
    * no in no out hits invariant, but * will be treated as Raw type, NoInNoOut<*> = NoInNoOut
    * -> NoInNoOut > NoInNoOut<Number>
    * -> NoInNoOut > NoInNoOut<Int>
    * -> NoInNoOut > NoInNoOut<String>
    *
    * Conclusion:
    * 3 beans matched: NoInNoOut<Number>, NoInNoOut<Int>, NoInNoOut<String>
    * */
    val starNoInNoOut: List<NoInNoOut<*>>,

    /*
    * Output:
    * property_to_inject: anyIns	resolved_type_to_match: List<In<Any?>>	matched_beans: 0
    *
    * Explanation:
    * in hits Contravariance
    * -> In<Any?> < In<Number> < In<Int>
    * -> In<Any?> < In<String>
    *
    * Conclusion:
    * no bean matched.
    * */
    val anyIns: List<In<Any?>>,

    /*
    * Output:
    * property_to_inject: anyOuts	resolved_type_to_match: List<Out<Any?>>	matched_beans: 3
    *
    * Explanation:
    * out hits Covariance
    * -> Out<Any?> > Out<Number> > Out<Int>
    * -> Out<Any?> > Out<String>
    *
    * Conclusion:
    * 3 beans matched: Out<Number>, Out<Int>, Out<String>
    * */
    val anyOuts: List<Out<Any?>>,

    /*
    * Output:
    * property_to_inject: anyNoInNoOut	resolved_type_to_match: List<NoInNoOut<Any?>>	matched_beans: 0
    *
    * Explanation:
    * no in on out hits invariant
    *
    * -> NoInNoOut<Any?> <> NoInNoOut<Number>
    * -> NoInNoOut<Any?> <> NoInNoOut<Int>
    * -> NoInNoOut<Any?> <> NoInNoOut<String>
    *
    * Conclusion:
    * no bean matched.
    * */
    val anyNoInNoOut: List<NoInNoOut<Any?>>,


    /*
    * Output:
    * property_to_inject: nothingIns	resolved_type_to_match: List<In<>>	matched_beans: 3
    *
    * Explanation:
    * in hits Contravariance, but Nothing will be treated as Raw type, In<Nothing> = In
    * -> In > In<Int> > In<Number>
    * -> In > In<String>
    *
    * Conclusion:
    * 3 beans matched: In<Number>, In<Int>, In<String>
    * */
    val nothingIns: List<In<Nothing>>,

    /*
    * Output:
    * property_to_inject: nothingOuts	resolved_type_to_match: List<Out<>>	matched_beans: 3
    *
    * Explanation:
    * out hits Covariance, but Nothing will be treated as Raw type, Out<Nothing> = Out
    * -> Out > Out<Number> > Out<Int>
    * -> Out > Out<String>
    *
    * Conclusion:
    * 3 beans matched: Out<Number>, Out<Int>, Out<String>
    * */
    val nothingOuts: List<Out<Nothing>>,

    /*
    * Output:
    * property_to_inject: nothingNoInNoOut	resolved_type_to_match: List<NoInNoOut<>>	matched_beans: 3
    *
    * Explanation:
    * no in no out hits invariant, but Nothing will be treated as Raw type, NoInNoOut<Nothing> = NoInNoOut
    * -> NoInNoOut > NoInNoOut<Number>
    * -> NoInNoOut > NoInNoOut<Int>
    * -> NoInNoOut > NoInNoOut<String>
    *
    * Conclusion:
    * 3 beans matched: NoInNoOut<Number>, NoInNoOut<Int>, NoInNoOut<String>
    * */
    val nothingNoInNoOut: List<NoInNoOut<Nothing>>,

    // ############################ Use-site variance: Type projection #############################


    /*
    * Projection is redundant: the corresponding type parameter of In has the same variance
    * List<In<in Number>> = List<In<Number>>
    * Output:
    * property_to_inject: numberInIns	resolved_type_to_match: List<In<Number>>	matched_beans: 1
    *
    * Explanation:
    * in hits Contravariance,
    * -> In<Number> < In<Int>
    *
    * Conclusion:
    * 1 bean matched: In<Number>
    * */
    val numberInIns: List<In<in Number>>,

    /*
    * Projection is redundant: the corresponding type parameter of In has the same variance
    * List<In<in Int>> = List<In<Int>>
    * Output:
    * property_to_inject: intInIns	resolved_type_to_match: List<In<Integer>>	matched_beans: 2
    *
    * Explanation:
    * in hits Contravariance,
    * -> In<Number> < In<Int>
    *
    * Conclusion:
    * 2 beans matched: In<Number>, In<Int>
    * */
    val intInIns: List<In<in Int>>,

    /*
    * Projection is required
    * Output:
    * property_to_inject: numberInNoInNoOut	resolved_type_to_match: List<NoInNoOut<Number>>	matched_beans: 1
    *
    * Explanation:
    * in hits Contravariance,
    * -> In<Number> < In<Int>
    *
    * Conclusion:
    * 1 bean matched: In<Number>
    * */
    val numberInNoInNoOut: List<NoInNoOut<in Number>>,

    /*
    * Projection is required
    * Output:
    * property_to_inject: intInNoInNoOut	resolved_type_to_match: List<NoInNoOut<Integer>>	matched_beans: 2
    *
    * Explanation:
    * in hits Contravariance,
    * -> In<Number> < In<Int>
    *
    * Conclusion:
    * 2 beans matched: In<Number>, In<Int>
    * */
    val intInNoInNoOut: List<NoInNoOut<in Int>>,


    /*
    * Projection is redundant: the corresponding type parameter of In has the same variance
    * List<Out<out Number>> = List<Out<Number>>
    * Output:
    * property_to_inject: numberOutOuts	resolved_type_to_match: List<Out<Number>>	matched_beans: 2
    *
    * Explanation:
    * out hits Covariance,
    * -> Out<Number> > Out<Int>
    *
    * Conclusion:
    * 2 beans matched: Out<Number>, Out<Int>
    * */
    val numberOutOuts: List<Out<out Number>>,

    /*
    * Projection is redundant: the corresponding type parameter of In has the same variance
    * Output:
    * property_to_inject: intOutIns	resolved_type_to_match: List<Out<Integer>>	matched_beans: 1
    *
    * Explanation:
    * out hits Covariance,
    * -> Out<Number> > Out<Int>
    *
    * Conclusion:
    * 1 bean matched: Out<Int>
    * */
    val intOutIns: List<Out<out Int>>,


    /*
    * Projection is required
    * Output:
    * property_to_inject: numberOutNoInNoOut	resolved_type_to_match: List<NoInNoOut<Number>>	matched_beans: 2
    *
    * Explanation:
    * out hits Covariance,
    * -> Out<Number> > Out<Int>
    *
    * Conclusion:
    * 2 beans matched: Out<Number>, Out<Int>
    * */
    val numberOutNoInNoOut: List<NoInNoOut<out Number>>,

    /*
    * Projection is required
    * Output:
    * property_to_inject: intOutNoInNoOut	resolved_type_to_match: List<NoInNoOut<Integer>>	matched_beans: 1
    *
    * Explanation:
    * out hits Covariance,
    * -> Out<Number> > Out<Int>
    *
    * Conclusion:
    * 1 bean matched: Out<Int>
    * */
    val intOutNoInNoOut: List<NoInNoOut<out Int>>

    // Projection is conflicting with variance of the corresponding type parameter of Out. Remove the projection or replace it with '*'
    // val intInOuts: List<Out<in Int>>, // can't compile, in/out conflicts
    // val numberOutIns: List<In<out Number>>, // can't compile, in/out conflicts

) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) = this::class.memberProperties
        .map { it as KProperty1<Any, List<*>> }.forEach(this::printGenerics)

    private fun printGenerics(kp: KProperty1<Any, List<*>>) = run {

        val t1 = ResolvableType.forField(kp.javaField!!)
        val t2 = t1.generics[0]
        val t3 = t2.generics[0]

        val typeName = resolvedTypeName(resolveClassName(t1), resolveClassName(t2), resolveClassName(t3))
        println("property_to_inject: ${kp.name}\tresolved_type_to_match: $typeName\tmatched_beans: ${kp.get(this).size}")
    }

    private fun resolvedTypeName(n1: String?, n2: String?, n3: String?) = "$n1<$n2<$n3>>"

    private fun resolveClassName(resolvableType: ResolvableType) = resolvableType.resolve()?.simpleName.let {
        when (it) {
            null -> ""
            "Object" -> "Any?"
            else -> it
        }
    }
}

/*
* PS: Why Generics can't be designed as Covariance, see below example by assuming it could be Covariance
*
class NumberProducer {

    private List<Number> queue;

    Producer(List<Number> queue) {
        this.queue = queue;
    }

    public void produce() {
        queue.add(0);
        queue.add(1L);
        queue.add(2.0d);
    }

}

class IntegerConsumer {

    private List<Integer> queue;

    IntegerConsumer(List<Integer> queue) {
        this.queue = queue;
    }

    public void consume() {
        for(Integer i : queue) {
            System.out.println(i);
        }
    }
}

List<Integer> queue = new ArrayList<>();
// Assume Generics is Covariance, it can be compiled
// because List<Integer> is subtype of  List<Number>
NumberProducer np = new NumberProducer(queue);
np.produce();

IntegerConsumer ic = new IntegerConsumer(queue)
// ClassCastException occurred when processing 1L, Long can't be casted to Integer
ic.consume();

Java Array is designed as Covariance, also can't avoid above issue.
class NumberProducer {

    private Number[] queue;

    Producer(Number[] queue) {
        this.queue = queue;
    }

    public void produce() {
        queue[0](0);
        queue[1](1L);
        queue[2](2.0d);
    }

}

class IntegerConsumer {

    Integer[] queue;

    IntegerConsumer(Integer[] queue) {
        this.queue = queue;
    }

    public void consume() {
        for(Integer i : queue) {
            System.out.println(i);
        }
    }
}

Integer[] queue = new Integer[100];
// Array is Covariance, it can be compiled
NumberProducer np = new NumberProducer(queue);
// ClassCastException occurred when processing 1L, Long can't be casted to Integer
np.produce();

we can see even though Covariance is intuitive, it will also bring troubles.
we can't just only use variance and invariant, the key point is how to tradeoff.

* in one word: generics should be designed as invariant basically, and can be limited variance.
* Java's solution:
* -> generics is invariant
* -> array is variance (even sometimes it's bad)
* -> limited variance: wildcard with bounds
*
* Kotlin's solution
* -> generics is invariant
* -> array is also invariant since array is also generics in kotlin
* -> limited variance: Declaration-site variance(using in and out) and Use-site variance: Type projection
*
*
*
*
*
* */