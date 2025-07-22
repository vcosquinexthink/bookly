package com.bookly.catalog.infrastructure.rest

import com.bookly.catalog.application.BookService
import com.bookly.catalog.application.BookstoreService
import com.bookly.catalog.domain.model.valueobject.BookId
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
    ): List<InventoryItemDto> {
        val bookstoresByProximity = bookstoreService.listBookstoresOrderedByProximity(location)
        return bookstoresByProximity.mapNotNull {
            val inventoryItem = it.getInventoryForBook(BookId(isbn))
            inventoryItem?.let { item ->
                val book = bookService.getBookById(item.bookId)
                book?.let { InventoryItemDto.from(item, book) }
            }
        }.filter { it.total > 0 }
    }
}
