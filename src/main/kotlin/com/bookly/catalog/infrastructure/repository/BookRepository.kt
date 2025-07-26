package com.bookly.catalog.infrastructure.repository

import com.bookly.catalog.domain.model.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository : JpaRepository<Book, String>

