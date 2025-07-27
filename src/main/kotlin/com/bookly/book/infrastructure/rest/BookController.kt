package com.bookly.book.infrastructure.rest

import com.bookly.book.domain.model.Book
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "Books API", description = "Endpoints for managing books")
interface BookController {
    @Operation(
        summary = "Create a new book",
        description = "Register a new book in the catalog",
        requestBody = RequestBody(
            description = "Book data",
            required = true,
            content = [Content(schema = Schema(implementation = BookDto::class))]
        ),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Book created successfully",
                content = [Content(schema = Schema(implementation = BookDto::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid book data",
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
