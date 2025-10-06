package com.example.bookfinder.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Past
import java.time.LocalDate

class AuthorModel (
    @field:NotNull(message = "{NotNull.author.id}")
    @field:JsonProperty("id")
    private val id: Int?,
    @field:NotBlank(message = "{NotBlank.author.name}")
    @field:JsonProperty("name")
    private val name: String?,
    @field:Past(message = "{Past.author.birthdate}")
    @field:JsonProperty("birthDate")
    private val birthDate: LocalDate?
) {
    @get:JsonIgnore
    val vId: Int get() = id!!
    @get:JsonIgnore
    val vName: String get() = name!!
    @get:JsonIgnore
    val vBirthDate: LocalDate get() = birthDate!!
}