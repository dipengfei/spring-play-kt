package me.danielpf.springplaykt

import kotlinx.coroutines.*
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

data class Row(val id: Int, val name: String)

interface RawDataExtractor {

    suspend fun onStart(context: MutableMap<String, Any?>)

    suspend fun onNext(context: MutableMap<String, Any?>, row: Row, index: Int)

    suspend fun onComplete(context: MutableMap<String, Any?>)

}

@Component
class ProductExtractor : RawDataExtractor {

    override suspend fun onStart(context: MutableMap<String, Any?>) = logger.info("init ProductExtractor..")


    override suspend fun onNext(context: MutableMap<String, Any?>, row: Row, index: Int) {
        logger.info("start processing product with row: {} -> {}", index, row)
        delay(1000)
        logger.info("end processing product with row: {} -> {}", index, row)
    }

    override suspend fun onComplete(context: MutableMap<String, Any?>) {
        logger.info("before product completing")
        delay(2000)
        logger.info("after product completing")
    }

    companion object : Log()
}

@Component
class ShopExtractor : RawDataExtractor {
    override suspend fun onStart(context: MutableMap<String, Any?>) = logger.info("init ShopExtractor..")

    override suspend fun onNext(context: MutableMap<String, Any?>, row: Row, index: Int) {
        logger.info("start processing shop with row: {} -> {}", index, row)
        delay(1500)
        logger.info("end processing shop with row: {} -> {}", index, row)
    }

    override suspend fun onComplete(context: MutableMap<String, Any?>) {
        logger.info("before shop completing")
        delay(2500)
        logger.info("after shop completing")
    }

    companion object : Log()
}

@Service
class ExtractService(private val extractors: List<RawDataExtractor>) {

    private val currentJob = AtomicReference<Job>()

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        extractors.forEach { logger.info("loading extractor: {}", it) }
    }

    fun process(n: Int) =
        dummyRows(n).let { rows ->
            // get each extractor ready and call onStart method
            extractors.map { extractor ->
                ConcurrentHashMap<String, Any?>() to extractor
            }.let { extractorPairs ->
                // create coroutines to run time-consuming data extract
                scope.launch {
                    // run each onStart
                    extractorPairs.map { async { it.second.onStart(it.first) } }.awaitAll()
                        .also {
                            // run each onNext
                            rows.forEachIndexed { index, row ->
                                if (isActive) {
                                    extractorPairs.map { async { it.second.onNext(it.first, row, index) } }.awaitAll()
                                }
                            }
                        }.also {
                            // finally run each onComplete
                            extractorPairs.map { async { it.second.onComplete(it.first) } }.awaitAll()
                        }
                }.also(currentJob::set)
            }
        }

    fun dummyRows(n: Int): MutableList<Row> =
        mutableListOf<Row>()
            .also { list ->
                for (i in 1..n) {
                    list += Row(i, String.format("row:%02d", i)).also { logger.info("dummy row: {}", it) }
                }
            }

    companion object : Log()

    fun cancel() = this.currentJob.get()?.cancel()

    fun isActive() = this.currentJob.get()?.isActive ?: false
}

@RestController
class ExtractController(private val extractService: ExtractService) {

    private val lock = ReentrantLock()

    @GetMapping("/start/{n}")
    fun start(@PathVariable("n") n: Int) = lock.withLock {
        if (extractService.isActive())
            "still_running"
        else
            extractService.process(n).let { "started" }
    }

    @GetMapping("/active")
    fun active() = lock.withLock { extractService.isActive().toString() }

    @GetMapping("/cancel")
    fun cancel() = lock.withLock {
        if (extractService.isActive()) extractService.cancel().let { "cancelled" } else "not_run"
    }
}

