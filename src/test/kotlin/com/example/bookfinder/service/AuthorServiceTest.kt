package com.example.bookfinder.service

import com.example.bookfinder.model.AuthorModel
import com.example.bookfinder.repository.AuthorRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class AuthorServiceTest {

    @MockK
    lateinit var authorRepository: AuthorRepository

    @InjectMockKs
    lateinit var authorService: AuthorService

    @Test
    fun `register should call repository and return saved author`() {
        val author = AuthorModel(
            id = 1,
            name = "John Doe",
            birthDate = LocalDate.of(1990, 1, 1)
        )

        every { authorRepository.register(author) } returns author

        val result = authorService.register(author)

        verify(exactly = 1) { authorRepository.register(author) }

        assertEquals(author, result)
    }

    @Test
    fun `update should call repository and return updated author`() {
        val author = AuthorModel(
            id = 2,
            name = "Jane Doe",
            birthDate = LocalDate.of(1985, 5, 15)
        )

        every { authorRepository.update(author) } returns author

        val result = authorService.update(author)

        verify(exactly = 1) { authorRepository.update(author) }
        assertEquals(author, result)
    }
}
