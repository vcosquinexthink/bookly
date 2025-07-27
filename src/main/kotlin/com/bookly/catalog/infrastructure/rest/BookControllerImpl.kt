package com.bookly.catalog.infrastructure.rest

import com.bookly.catalog.application.BookService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/books")
class BookControllerImpl(
    private val bookService: BookService
) : BookController {

    @PostMapping
    override fun createBook(@RequestBody bookDto: BookDto): ResponseEntity<BookDto> {
        return ResponseEntity.ok(
            BookDto.fromDomain(bookService.addOrUpdateBookReference(bookDto.toBook()))
        )
    }
}

