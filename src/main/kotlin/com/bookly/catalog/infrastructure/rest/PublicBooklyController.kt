package com.bookly.catalog.infrastructure.rest

import com.bookly.catalog.application.BookstoreService
import com.bookly.catalog.domain.model.valueobject.BookId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/public")
class PublicBooklyController(private val bookstoreService: BookstoreService) {

    @GetMapping("/bookstores")
    fun listBookstores(): List<BookstoreDto> =
        bookstoreService.listBookstores().map { BookstoreDto.from(it) }

    @GetMapping("/bookstores/{id}/inventory/{isbn}")
    fun getInventoryForBook(
        @PathVariable id: UUID,
        @PathVariable isbn: String
    ): InventoryItemDto? {
        val item = bookstoreService.getInventoryForBook(id, BookId(isbn))
        return item?.let { InventoryItemDto.from(it) }
    }

    @GetMapping("/bookstores/{id}/inventory")
    fun listInventory(@PathVariable id: UUID): List<InventoryItemDto> =
        bookstoreService.listInventory(id).map { InventoryItemDto.from(it) }
}
