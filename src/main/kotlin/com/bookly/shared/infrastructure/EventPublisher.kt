package com.bookly.shared.infrastructure

interface EventPublisher {
    fun publish(event: com.bookly.shared.domain.event.DomainEvent)
}

