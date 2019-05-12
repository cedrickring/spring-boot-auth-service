package dev.cedrickring.auth

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AuthServiceApplication

fun main(args: Array<String>) {
    runApplication<AuthServiceApplication>(*args)
}

inline fun <reified T: Any> loggerFor(): Logger = LoggerFactory.getLogger(T::class.java)
