package com.example.bookfinder.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class BookModel(
    @field:NotNull(message = "{NotNull.book.id}")
    @field:JsonProperty("id")
    private val id: Int?,
    @field:NotBlank(message = "{NotBlank.book.title}")
    @field:JsonProperty("title")
    private val title: String?,
    @field:NotNull(message = "{NotNull.book.price}")
    @field:Min(0, message = "{Min.book.price}")
    @field:JsonProperty("price")
    private val price: Int?,
    @field:NotNull(message = "{NotNull.book.isPublished}")
    @field:JsonProperty("isPublished")
    private val isPublished: Boolean?,
    @field:NotNull(message = "{NotNull.book.authorList}")
    @field:Size(min = 1, message = "{Size.book.authorList}")
    @field:JsonProperty("authorIdList")
    private val authorIdList: List<Int>?
) {
    @get:JsonIgnore
    val vId: Int get() = id!!
    @get:JsonIgnore
    val vTitle: String get() = title!!
    @get:JsonIgnore
    val vPrice: Int get() = price!!
    @get:JsonIgnore
    val vIsPublished: Boolean get() = isPublished!!
    @get:JsonIgnore
    val vAuthorIdList: List<Int> get() = authorIdList!!
}