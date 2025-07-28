package com.bookly.book.infrastructure.rest

import com.bookly.book.domain.model.Book
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "Books API", description = "Endpoints for managing books in the catalog")
interface BookController {
    @Operation(
        summary = "Create a new book",
        description = "Register a new book in the catalog system with ISBN, title, and author information",
        requestBody = RequestBody(
            description = "Book information including ISBN, title, and author",
            required = true,
            content = [Content(schema = Schema(implementation = BookDto::class))]
        ),
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "Book created successfully",
                content = [Content(schema = Schema(implementation = BookDto::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid book data provided",
                content = [Content(schema = Schema())]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Book with this ISBN already exists",
                content = [Content(schema = Schema())]
            )
        ]
    )
    fun createBook(bookDto: BookDto): ResponseEntity<BookDto>
}

data class BookDto(val isbn: String, val title: String, val author: String) {
    fun toBook(): Book = Book(
        Book.BookId(isbn), Book.BookTitle(title), Book.BookAuthor(author)
    )

    companion object {
        fun fromDomain(book: Book) = BookDto(book.getBookId().isbn, book.getTitle().value, book.getAuthor().value)
    }
}
