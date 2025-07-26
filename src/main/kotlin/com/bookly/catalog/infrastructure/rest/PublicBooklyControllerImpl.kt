package com.bookly.catalog.infrastructure.rest

import com.bookly.catalog.application.BookService
import com.bookly.catalog.application.BookstoreService
import com.bookly.catalog.domain.model.valueobject.BookId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/public")
class PublicBooklyControllerImpl(
    private val bookstoreService: BookstoreService,
    private val bookService: BookService
) : PublicBooklyController {

    @GetMapping("/inventory/search")
    override fun searchInventoryByIsbnAndLocation(
        @RequestParam isbn: String,
        @RequestParam location: Int
    ): ResponseEntity<List<InventoryItemDto>> {
        val book = bookService.getBookById(BookId(isbn)) ?: return ResponseEntity.notFound().build()
        val bookstoresByProximity = bookstoreService.listBookstoresOrderedByProximity(location)
        val inventoryItems = bookstoresByProximity.mapNotNull {
            val inventoryItem = it.getInventoryForBookId(BookId(isbn))
            inventoryItem?.let { item ->
                InventoryItemDto.fromInventoryItemAndBook(item, book)
            }
        }.filter { it.total > 0 }
        return ResponseEntity.ok(inventoryItems)
    }

    @GetMapping("/bookstores/search")
    override fun searchBookstoresByProximity(
        @RequestParam location: Int
    ): ResponseEntity<List<BookstoreDto>> {
        val bookstores = bookstoreService.listBookstoresOrderedByProximity(location)
        val response = bookstores.map { BookstoreDto.fromBookstore(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/bookstores/{storeId}/catalog")
    override fun getBookstoreCatalog(
        @PathVariable storeId: UUID
    ): ResponseEntity<List<InventoryItemDto>> {
        val catalog = bookstoreService.getCatalogByBookstoreId(storeId)
        val response = catalog.map { item ->
            val book = bookService.getBookById(item.bookId)
                ?: throw IllegalStateException("Book with ID ${item.bookId} not found")
            InventoryItemDto.fromInventoryItemAndBook(item, book)
        }
        return ResponseEntity.ok(response)
    }
}
