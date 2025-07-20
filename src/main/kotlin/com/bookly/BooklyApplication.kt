package com.bookly

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BooklyApplication

fun main(args: Array<String>) {
    runApplication<BooklyApplication>(*args)
}

