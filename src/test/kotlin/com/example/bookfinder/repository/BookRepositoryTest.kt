package com.example.bookfinder.repository

import com.example.bookfinder.constants.ErrorMessages
import com.example.bookfinder.error.exception.DbAccessException
import com.example.bookfinder.model.BookModel
import com.example.db.tables.Author
import com.example.db.tables.Book
import org.jooq.DSLContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDate

@JooqTest
@Import(BookRepository::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class BookRepositoryTest @Autowired constructor(
    private val dsl: DSLContext,
    private val repository: BookRepository
) {

    companion object {
        @Container
        @JvmStatic
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("test_library")
            .withUsername("test")
            .withPassword("test")

        @JvmStatic
        @DynamicPropertySource
        fun registerProps(reg: DynamicPropertyRegistry) {
            reg.add("spring.datasource.url", postgres::getJdbcUrl)
            reg.add("spring.datasource.username", postgres::getUsername)
            reg.add("spring.datasource.password", postgres::getPassword)
            reg.add("spring.jooq.sql-dialect") { "POSTGRES" }
        }
    }

    @BeforeEach
    fun clean() {
        dsl.deleteFrom(Book.BOOK).execute()
        dsl.deleteFrom(Author.AUTHOR).execute()
    }

    @Test
    fun `register should insert and return book`() {
        val authorId = dsl.insertInto(Author.AUTHOR)
            .set(Author.AUTHOR.ID, 1)
            .set(Author.AUTHOR.NAME, "John Doe")
            .set(Author.AUTHOR.BIRTH_DATE, LocalDate.of(1970, 1, 1))
            .execute()

        val model = BookModel(
            id = 0,
            title = "Kotlin Master",
            price = 2000,
            isPublished = true,
            authorIdList = listOf(1)
        )

        val saved = repository.register(model)

        assertNotNull(saved.vId)
        assertEquals("Kotlin Master", saved.vTitle)
        assertTrue(saved.vIsPublished)
    }

    @Test
    fun `get should return book when it exists`() {
        // 1. author を登録
        val authorId = dsl.insertInto(Author.AUTHOR)
            .set(Author.AUTHOR.ID, 1)
            .set(Author.AUTHOR.NAME, "John Doe")
            .set(Author.AUTHOR.BIRTH_DATE, LocalDate.of(1970, 1, 1))
            .returning(Author.AUTHOR.ID)
            .fetchOne()!!
            .getValue(Author.AUTHOR.ID)

        // 2. book を登録
        val inserted = repository.register(
            BookModel(
                id = 0,
                title = "Kotlin Master",
                price = 3500,
                isPublished = true,
                authorIdList = listOf(authorId)
            )
        )

        // 3. getで取得
        val result = repository.get(inserted.vId)

        // 4. 検証
        assertEquals(inserted.vId, result.vId)
        assertEquals("Kotlin Master", result.vTitle)
        assertEquals(3500, result.vPrice)
        assertTrue(result.vIsPublished)
    }


    @Test
    fun `get should throw DbAccessException when book does not exist`() {
        val ex = assertThrows<DbAccessException> {
            repository.get(9999)
        }
        assertEquals(ErrorMessages.DB_ACCESS_ERROR, ex.message)
    }

    @Test
    fun `update should modify existing book and authors`() {
        val authorId1 = dsl.insertInto(Author.AUTHOR)
            .set(Author.AUTHOR.ID, 1)
            .set(Author.AUTHOR.NAME, "John Doe")
            .set(Author.AUTHOR.BIRTH_DATE, LocalDate.of(1970, 1, 1))
            .returning(Author.AUTHOR.ID)
            .fetchOne()!!
            .getValue(Author.AUTHOR.ID)

        val authorId2 = dsl.insertInto(Author.AUTHOR)
            .set(Author.AUTHOR.ID, 2)
            .set(Author.AUTHOR.NAME, "Jane Doe")
            .set(Author.AUTHOR.BIRTH_DATE, LocalDate.of(1980, 2, 2))
            .returning(Author.AUTHOR.ID)
            .fetchOne()!!
            .getValue(Author.AUTHOR.ID)

        val baseBook = repository.register(
            BookModel(
                id = 0,
                title = "Original",
                price = 1500,
                isPublished = false,
                authorIdList = listOf(authorId1)
            )
        )

        val updatedBook = repository.update(
            BookModel(
                id = baseBook.vId,
                title = "Updated Title",
                price = 2000,
                isPublished = true,
                authorIdList = listOf(authorId2)
            )
        )

        assertEquals("Updated Title", updatedBook.vTitle)
        assertEquals(2000, updatedBook.vPrice)
        assertTrue(updatedBook.vIsPublished)
        assertEquals(listOf(authorId2), updatedBook.vAuthorIdList)

        val dbRecord = dsl.selectFrom(Book.BOOK)
            .where(Book.BOOK.ID.eq(baseBook.vId))
            .fetchOne()!!
        assertEquals("Updated Title", dbRecord.get(Book.BOOK.TITLE))
        assertEquals(2000, dbRecord.get(Book.BOOK.PRICE))
        assertTrue(dbRecord.get(Book.BOOK.IS_PUBLISHED))
    }
}
