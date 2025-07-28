package com.bookly.catalog.infrastructure.rest

import com.bookly.catalog.application.BookstoreService
import com.bookly.catalog.domain.model.BookstoreName
import com.bookly.catalog.domain.model.Location
import com.bookly.catalog.infrastructure.rest.BookstoreDto.Companion.fromDomain
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/bookly/bookstores/bookstores")
class InternalBookstoreControllerImpl(
    private val bookstoreService: BookstoreService,
) : InternalBookstoreController {

    @PostMapping
    override fun createBookstore(
        @RequestBody request: CreateBookstoreRequest
    ): ResponseEntity<BookstoreDto> {
        return ResponseEntity.ok(
            fromDomain(bookstoreService.createBookstore(BookstoreName(request.name), Location(request.location)))
        )
    }
}