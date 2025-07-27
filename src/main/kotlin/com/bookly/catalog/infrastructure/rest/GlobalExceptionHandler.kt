package com.bookly.catalog.infrastructure.rest

import com.bookly.book.application.BookNotFoundException
import com.bookly.catalog.application.BookstoreService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BookstoreService.BookstoreNotFoundException::class)
    fun handleBookstoreNotFoundException(ex: BookstoreService.BookstoreNotFoundException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(ex.message)
    }

    @ExceptionHandler(BookNotFoundException::class)
    fun handleBookstoreNotFoundException(ex: BookNotFoundException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(ex.message)
    }
}
