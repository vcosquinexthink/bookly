package com.bookly.catalog.application

import com.bookly.catalog.domain.model.InventoryItem
import com.bookly.catalog.domain.model.valueobject.BookId
import com.bookly.catalog.domain.model.valueobject.BookstoreId
import org.springframework.stereotype.Service

@Service
class InventoryService(private val inventories: MutableMap<Pair<BookstoreId, BookId>, InventoryItem> = mutableMapOf()) {

    fun addInventoryItem(bookstoreId: BookstoreId, bookId: BookId, count: Int = 1): InventoryItem {
        val key = Pair(bookstoreId, bookId)
        val previousInventory = inventories[key]
        val newTotal = (previousInventory?.total ?: 0) + count
        val newAvailable = (previousInventory?.available ?: 0) + count
        val newInventory = InventoryItem(bookId, newTotal, newAvailable, bookstoreId)
        inventories[key] = newInventory
        return newInventory
    }

    fun getInventory(bookstoreId: BookstoreId, bookId: BookId): InventoryItem {
        return inventories[Pair(bookstoreId, bookId)] ?: InventoryItem(bookId, 0, 0, bookstoreId)
    }

    fun getInventoriesForBookstore(bookstoreId: BookstoreId): Array<InventoryItem> {
        return inventories.filter { it.key.first == bookstoreId }
            .map { it.value }
            .toTypedArray()
    }
}
