package com.bookly.catalog.domain.model.valueobject

import java.math.BigDecimal

data class Price(val amount: BigDecimal, val currency: String)

