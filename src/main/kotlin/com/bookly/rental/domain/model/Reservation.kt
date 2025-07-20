package com.bookly.rental.domain.model

import com.bookly.rental.domain.model.valueobject.ReservationId
import com.bookly.rental.domain.model.valueobject.RentalPeriod

class Reservation(
    val id: ReservationId,
    val bookId: String,
    val userId: String,
    val period: RentalPeriod
)

