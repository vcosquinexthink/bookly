package com.bookly.reservation.infrastructure.rest

import com.bookly.book.application.BookService
import com.bookly.book.domain.model.Book
import com.bookly.bookstore.application.BookstoreService
import com.bookly.inventory.application.InventoryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/bookly/reservation")
class ReservationControllerImpl(
    private val bookstoreService: BookstoreService,
    private val bookService: BookService,
    private val inventoryService: InventoryService
) : ReservationController {

    @PostMapping
    override fun createReservation(@RequestBody reservationRequest: ReservationRequestDto): ResponseEntity<ReservationDto> {
        //TODO("Not yet implemented")

        //val book = bookService.getBookById(Book.BookId(isbn))
        return ResponseEntity.ok(ReservationDto(UUID.randomUUID()))
    }
}
