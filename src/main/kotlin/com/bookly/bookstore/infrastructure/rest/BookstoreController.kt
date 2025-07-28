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

@Tag(name = "Bookstores API", description = "Endpoints for managing bookstore locations and information")
interface BookstoreController {

    @Operation(
        summary = "Create a new bookstore",
        description = "Register a new bookstore location in the system with name and location information"
    )
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "201",
            description = "Bookstore created successfully",
            content = [Content(schema = Schema(implementation = BookstoreDto::class))]
        ), ApiResponse(
            responseCode = "400",
            description = "Invalid bookstore data provided",
            content = [Content(schema = Schema())]
        )]
    )
    fun createBookstore(request: CreateBookstoreRequest): ResponseEntity<BookstoreDto>


    @Operation(
        summary = "Search bookstores by proximity to location",
        description = "Retrieve all bookstores in the system ordered by their proximity to a specified location code, with closest bookstores appearing first"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Bookstores retrieved and ordered by proximity successfully",
                content = [Content(schema = Schema(implementation = Array<BookstoreDto>::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid location parameter provided",
                content = [Content(schema = Schema())]
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
