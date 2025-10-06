package com.example.bookfinder.model

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookModelTest {
    private lateinit var validator: Validator

    @BeforeAll
    fun setup() {
        validator = Validation.buildDefaultValidatorFactory().validator
    }

    @Test
    fun `id should not be null`() {
        val model = BookModel(
            id = null,
            title = "Spring Boot",
            price = 2500,
            isPublished = true,
            authorIdList = listOf(1)
        )

        val violations = validator.validate(model)
        assertTrue(violations.any { it.message == "{NotNull.book.id}" })
    }

    @Test
    fun `title should not be blank`() {
        val model = BookModel(
            id = 1,
            title = "",
            price = 2500,
            isPublished = true,
            authorIdList = listOf(1)
        )

        val violations = validator.validate(model)
        assertTrue(violations.any { it.message == "{NotBlank.book.title}" })
    }

    @Test
    fun `price should not be null and must be at least 0`() {
        val model1 = BookModel(
            id = 1,
            title = "Spring Boot",
            price = null,
            isPublished = true,
            authorIdList = listOf(1)
        )
        val model2 = BookModel(
            id = 1,
            title = "Spring Boot",
            price = -10,
            isPublished = true,
            authorIdList = listOf(1)
        )

        val violations1 = validator.validate(model1)
        assertTrue(violations1.any { it.message == "{NotNull.book.price}" })

        val violations2 = validator.validate(model2)
        assertTrue(violations2.any { it.message == "{Min.book.price}" })
    }

    @Test
    fun `isPublished should not be null`() {
        val model = BookModel(
            id = 1,
            title = "Spring Boot",
            price = 2500,
            isPublished = null,
            authorIdList = listOf(1)
        )

        val violations = validator.validate(model)
        assertTrue(violations.any { it.message == "{NotNull.book.isPublished}" })
    }

    @Test
    fun `authorIdList should not be null and must have at least one element`() {
        val model1 = BookModel(
            id = 1,
            title = "Spring Boot",
            price = 2500,
            isPublished = true,
            authorIdList = null
        )
        val model2 = BookModel(
            id = 1,
            title = "Spring Boot",
            price = 2500,
            isPublished = true,
            authorIdList = emptyList()
        )

        val violations1 = validator.validate(model1)
        assertTrue(violations1.any { it.message == "{NotNull.book.authorList}" })

        val violations2 = validator.validate(model2)
        assertTrue(violations2.any { it.message == "{Size.book.authorList}" })
    }

}