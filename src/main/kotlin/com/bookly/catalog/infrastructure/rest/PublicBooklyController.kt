package com.bookly.catalog.infrastructure.rest

import com.bookly.catalog.application.BookService
import com.bookly.catalog.application.BookstoreService
import com.bookly.catalog.domain.model.valueobject.BookId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/public")
class PublicBooklyController(
    private val bookstoreService: BookstoreService,
    private val bookService: BookService
) {

    @GetMapping("/inventory/search")
    fun searchInventoryByIsbnAndLocation(
        @RequestParam isbn: String,
        @RequestParam location: Int
    ): ResponseEntity<List<InventoryItemDto>> {
        val book = bookService.getBookById(BookId(isbn)) ?: return ResponseEntity.notFound().build()
        val bookstoresByProximity = bookstoreService.listBookstoresOrderedByProximity(location)
        val inventoryItems = bookstoresByProximity.mapNotNull {
            val inventoryItem = it.getInventoryForBook(BookId(isbn))
            inventoryItem?.let { item ->
                InventoryItemDto.from(item, book)
            }
        }.filter { it.total > 0 }
        return ResponseEntity.ok(inventoryItems)
    }

    @GetMapping("/bookstores/search")
    fun searchBookstoresByProximity(@RequestParam location: Int): ResponseEntity<List<BookstoreDto>> {
        val bookstores = bookstoreService.listBookstoresOrderedByProximity(location)
        val response = bookstores.map { BookstoreDto.from(it) }
        return ResponseEntity.ok(response)
    }
}
