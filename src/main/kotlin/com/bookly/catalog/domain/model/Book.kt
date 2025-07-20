package com.bookly.catalog.domain.model

import com.bookly.catalog.domain.model.valueobject.BookId
import java.util.UUID

class Book(
    val id: BookId,
    val title: String,
    val author: String,
    val rentalPolicy: RentalPolicy
)
