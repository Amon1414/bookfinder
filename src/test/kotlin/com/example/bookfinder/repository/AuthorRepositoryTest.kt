package com.example.bookfinder.repository

import com.example.bookfinder.constants.ErrorMessages
import com.example.bookfinder.error.exception.DbAccessException
import com.example.bookfinder.model.AuthorModel
import com.example.db.tables.Author
import org.jooq.DSLContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.jdbc.Sql
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDate

@JooqTest
@Import(AuthorRepository::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Sql(scripts = ["classpath:test_data.sql"])
class AuthorRepositoryTest @Autowired constructor(
    private val dsl: DSLContext,
    private val repository: AuthorRepository
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
        dsl.deleteFrom(Author.AUTHOR).execute()
    }

    @Test
    fun `register should insert and return new author`() {
        val input = AuthorModel(
            id = 0,
            name = "John Doe",
            birthDate = LocalDate.of(1970, 1, 1)
        )

        val saved = repository.register(input)

        assertNotNull(saved.vId)
        assertEquals("John Doe", saved.vName)
        assertEquals(LocalDate.of(1970, 1, 1), saved.vBirthDate)

        // DB 実体も確認
        val row = dsl.selectFrom(Author.AUTHOR)
            .where(Author.AUTHOR.ID.eq(saved.vId))
            .fetchOne()
        assertNotNull(row)
        assertEquals("John Doe", row!!.get(Author.AUTHOR.NAME))
    }

    @Test
    fun `update should modify and return updated author`() {
        // まず登録
        val base = repository.register(
            AuthorModel(0, "Before", LocalDate.of(1980, 5, 5))
        )

        // 更新
        val updated = repository.update(
            AuthorModel(base.vId, "After", base.vBirthDate)
        )

        assertEquals(base.vId, updated.vId)
        assertEquals("After", updated.vName)

        val row = dsl.selectFrom(Author.AUTHOR).where(Author.AUTHOR.ID.eq(base.vId)).fetchOne()
        assertEquals("After", row!!.get(Author.AUTHOR.NAME))
    }

    @Test
    fun `update should throw DbAccessException when target row does not exist`() {
        val ex = assertThrows(DbAccessException::class.java) {
            repository.update(AuthorModel(9999, "Nobody", LocalDate.of(1999, 9, 9)))
        }
        assertEquals(ErrorMessages.DB_ACCESS_ERROR, ex.message)
    }
}
