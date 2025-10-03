package com.example.bookfinder.service

import com.example.bookfinder.constants.ErrorMessages
import com.example.bookfinder.error.exception.InvalidOperationException
import com.example.bookfinder.model.BookModel
import com.example.bookfinder.repository.BookRepository
import org.springframework.stereotype.Service

@Service
class BookService(private val bookRepository: BookRepository) {

    /**
     * Get the books by author.
     *
     * @param authorName author name.
     */
    fun get(authorName: String): List<BookModel> {
        return bookRepository.getByAuthor(authorName)
    }

    /**
     * Register the book.
     *
     * @param book book model to register.
     */
    fun register(book: BookModel): BookModel {
        return bookRepository.register(book)
    }

    /**
     * Update the book.
     *
     * @param book book model to update.
     */
    fun update(book: BookModel): BookModel {
        val isPublished: Boolean = bookRepository.get(book.vId).vIsPublished
        if (isPublished && !book.vIsPublished) {
            throw InvalidOperationException(ErrorMessages.INVALID_OPERATION_PUBLISH_FLAG)
        }
        return bookRepository.update(book)
    }
}