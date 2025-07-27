package com.bookly.book.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "books")
class Book(
    @Id
    @Column
    private val idValue: String,
    @Column(name = "title")
    private val titleValue: String,
    @Column(name = "author")
    private val authorValue: String
) {
    constructor(bookId: BookId, title: BookTitle, author: BookAuthor) : this(
        bookId.isbn,
        title.value,
        author.value
    )

    fun getBookId(): BookId = BookId(idValue)
    fun getTitle(): BookTitle = BookTitle(titleValue)
    fun getAuthor(): BookAuthor = BookAuthor(authorValue)
    data class BookId(val isbn: String)
    data class BookTitle(val value: String)
    data class BookAuthor(val value: String)
}
