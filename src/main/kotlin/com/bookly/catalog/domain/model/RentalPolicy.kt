package com.bookly.catalog.domain.model

import com.bookly.catalog.domain.model.valueobject.Price

data class RentalPolicy(
    val pricePerDay: Price,
    val maxDurationDays: Int
)

