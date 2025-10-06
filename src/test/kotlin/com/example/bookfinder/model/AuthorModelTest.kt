package com.example.bookfinder.model

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import java.time.LocalDate
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthorModelTest {

    private lateinit var validator: Validator

    @BeforeAll
    fun setup() {
        validator = Validation.buildDefaultValidatorFactory().validator
    }

    @Test
    fun `id should not be null`() {
        val model = AuthorModel(
            id = null,
            name = "John Doe",
            birthDate = LocalDate.of(1990, 1, 1)
        )

        val violations = validator.validate(model)

        assertTrue(violations.any { it.message == "{NotNull.author.id}" })
    }

    @Test
    fun `name should not be blank`() {
        val model = AuthorModel(
            id = 1,
            name = "",
            birthDate = LocalDate.of(1990, 1, 1)
        )

        val violations = validator.validate(model)

        assertTrue(violations.any { it.message == "{NotBlank.author.name}" })
    }

    @Test
    fun `birthDate should be in the past`() {
        val model = AuthorModel(
            id = 1,
            name = "John Doe",
            birthDate = LocalDate.now().plusDays(1) 
        )

        val violations = validator.validate(model)

        assertTrue(violations.any { it.message == "{Past.author.birthdate}" })
    }

    @Test
    fun `valid author should have no violations`() {
        val model = AuthorModel(
            id = 1,
            name = "John Doe",
            birthDate = LocalDate.of(1990, 1, 1)
        )

        val violations = validator.validate(model)

        assertEquals(0, violations.size)
    }
}