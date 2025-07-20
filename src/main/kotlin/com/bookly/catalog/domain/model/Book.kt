package com.bookly.catalog.domain.model

import com.bookly.catalog.domain.model.valueobject.BookAuthor
import com.bookly.catalog.domain.model.valueobject.BookId
import com.bookly.catalog.domain.model.valueobject.BookTitle

class Book(
    val id: BookId,
    val title: BookTitle,
    val author: BookAuthor,
    val rentalPolicy: RentalPolicy
)
