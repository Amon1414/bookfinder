package com.example.bookfinder.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Past
import java.time.LocalDate

class AuthorModel (
    @field:NotNull(message = "{NotNull.author.id}")
    val id: Int?,
    @field:NotBlank(message = "{NotBlank.author.name}")
    val name: String?,
    @field:Past(message = "{Past.author.birthdate}")
    val birthDate: LocalDate?
) {

}