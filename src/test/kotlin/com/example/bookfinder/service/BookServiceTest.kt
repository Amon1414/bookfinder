package com.example.bookfinder.service

import com.example.bookfinder.constants.ErrorMessages
import com.example.bookfinder.error.exception.InvalidOperationException
import com.example.bookfinder.model.BookModel
import com.example.bookfinder.repository.BookRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.Test

@ExtendWith(MockKExtension::class)
class BookServiceTest {

    @MockK
    lateinit var bookRepository: BookRepository

    @InjectMockKs
    lateinit var bookService: BookService

    @Test
    fun `get should call repository and return list of books`() {
        val books = listOf(
            BookModel(id = 1, title = "Spring Boot", price = 2500, isPublished = true, authorIdList = listOf(1)),
            BookModel(id = 2, title = "Kotlin Master", price = 5000, isPublished = false, authorIdList = listOf(1))
        )

        every { bookRepository.getByAuthor(1) } returns books

        val result = bookService.get(1)

        verify(exactly = 1) { bookRepository.getByAuthor(1) }
        assertEquals(books, result)
    }

    @Test
    fun `register should call repository and return registered book`() {
        val book = BookModel(1, "New Book", 2000, false, listOf(1))
        every { bookRepository.register(book) } returns book

        val result = bookService.register(book)

        verify(exactly = 1) { bookRepository.register(book) }
        assertEquals(book, result)
    }

    @Test
    fun `update should call repository and return updated book`() {
        val existing = BookModel(id = 1, title = "Spring Boot", price = 2500, isPublished = false, authorIdList = listOf(1))
        val updateRequest = BookModel(id = 1, title = "Kotlin Master", price = 5000, isPublished = false, authorIdList = listOf(2))
        val updateResponse = BookModel(id = 1, title = "Kotlin Master", price = 5000, isPublished = false, authorIdList = listOf(2))

        every { bookRepository.get(1) } returns existing
        every { bookRepository.update(updateRequest) } returns updateResponse

        val result = bookService.update(updateRequest)

        verify(exactly = 1) { bookRepository.get(1) }
        verify(exactly = 1) { bookRepository.update(updateRequest) }
        assertEquals(updateResponse, result)
    }

    @Test
    fun `update should throw exception when trying to unpublish an already published book`() {
        val existing = BookModel(id = 1, title = "Spring Boot", price = 2500, isPublished = true, authorIdList = listOf(1))
        val updated = BookModel(id = 1, title = "Kotlin Master", price = 5000, isPublished = false, authorIdList = listOf(2))

        every { bookRepository.get(1) } returns existing
        every { bookRepository.update(updated) } returns updated
        every { bookRepository.update(updated) } returns updated

        val exception = assertThrows<InvalidOperationException> {
            bookService.update(updated)
        }

        assertEquals(ErrorMessages.INVALID_OPERATION_PUBLISH_FLAG, exception.message)
        verify(exactly = 1) { bookRepository.get(1) }
        verify(exactly = 0) { bookRepository.update(any()) }
    }
}
