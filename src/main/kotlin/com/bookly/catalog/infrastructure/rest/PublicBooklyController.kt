package com.bookly.catalog.infrastructure.rest

import com.bookly.catalog.application.BookstoreService
import com.bookly.catalog.domain.model.valueobject.BookId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/public")
class PublicBooklyController(private val bookstoreService: BookstoreService) {

    @GetMapping("/inventory/search")
    fun searchInventoryByIsbnAndLocation(
        @RequestParam isbn: String,
        @RequestParam location: Int
    ): List<InventoryItemDto> {
        val bookstores = bookstoreService.listBookstores().filter { it.location.value == location }
        return bookstores.mapNotNull { bookstore ->
            val item = bookstore.getInventoryForBook(BookId(isbn))
            item?.let { InventoryItemDto.from(it) }
        }.filter { it.total > 0 }
    }
}
