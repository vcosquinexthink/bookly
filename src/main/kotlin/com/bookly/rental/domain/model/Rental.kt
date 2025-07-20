package com.bookly.rental.domain.model

import com.bookly.rental.domain.model.valueobject.RentalPeriod
import com.bookly.rental.domain.model.valueobject.RentalStatus

class Rental(
    val id: String,
    val reservationId: String,
    val period: RentalPeriod,
    val status: RentalStatus
)

