package com.bookly.catalog.infrastructure.rest

import com.bookly.catalog.application.BookstoreService
import com.bookly.catalog.domain.model.BookstoreName
import com.bookly.catalog.domain.model.Location
import com.bookly.catalog.infrastructure.rest.BookstoreDto.Companion.fromDomain
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/bookly/bookstores")
class BookstoreControllerImpl(
    private val bookstoreService: BookstoreService,
) : BookstoreController {

    @PostMapping
    override fun createBookstore(
        @RequestBody request: CreateBookstoreRequest
    ): ResponseEntity<BookstoreDto> {
        return ResponseEntity.ok(
            fromDomain(bookstoreService.createBookstore(BookstoreName(request.name), Location(request.location)))
        )
    }

    @GetMapping("/search")
    override fun searchBookstoresByProximity(
        @RequestParam location: Int
    ): ResponseEntity<List<BookstoreDto>> {
        return ResponseEntity.ok(
            bookstoreService.listBookstoresOrderedByProximity(location).map { BookstoreDto.fromDomain(it) }
        )
    }
}