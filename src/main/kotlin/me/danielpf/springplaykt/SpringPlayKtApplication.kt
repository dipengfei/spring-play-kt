package me.danielpf.springplaykt

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringPlayKtApplication

fun main(args: Array<String>) {
    runApplication<SpringPlayKtApplication>(*args)
}

abstract class Log {
    val logger: Logger = LoggerFactory.getLogger(this.javaClass)
}




