package com.bookly.bookstore.infrastructure.rest

import com.bookly.bookstore.domain.model.Bookstore
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import java.util.*

@Tag(name = "Bookstores API", description = "Endpoints for managing bookstores")
interface BookstoreController {

    @Operation(
        summary = "Create a new bookstore", description = "Register a new bookstore into the system"
    )
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Bookstore created successfully",
            content = [Content(schema = Schema(implementation = BookstoreDto::class))]
        )]
    )
    fun createBookstore(request: CreateBookstoreRequest): ResponseEntity<BookstoreDto>


    @Operation(
        summary = "Search bookstores by proximity",
        description = "Get all bookstores ordered by proximity to the specified location"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Bookstores retrieved successfully",
                content = [Content(schema = Schema(implementation = BookstoreDto::class))]
            )
        ]
    )
    fun searchBookstoresByProximity(
        @Parameter(description = "Location code for proximity search", required = true)
        location: Int
    ): ResponseEntity<List<BookstoreDto>>
}


data class CreateBookstoreRequest(val name: String, val location: Int)

data class BookstoreDto(val id: UUID, val name: String, val location: Int) {
    companion object {
        fun fromDomain(bookstore: Bookstore) =
            BookstoreDto(bookstore.id.value, bookstore.name.value, bookstore.location.value)
    }
}
