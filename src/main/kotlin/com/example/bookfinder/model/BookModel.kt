package com.example.bookfinder.model

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class BookModel(
    @field:NotNull(message = "{NotNull.book.id}")
    private val id: Int?,
    @field:NotBlank(message = "{NotBlank.book.title}")
    private val title: String?,
    @field:NotNull(message = "{NotNull.book.price}")
    @field:Min(0, message = "{Min.book.price}")
    private val price: Int?,
    @field:NotNull(message = "{NotNull.book.isPublished}")
    private val isPublished: Boolean?,
    @field:NotNull(message = "{NotNull.book.authorList}")
    @field:Size(min = 1, message = "{Size.book.authorList}")
    private val authorIdList: List<Int>?
) {
    val vId: Int get() = id!!
    val vTitle: String get() = title!!
    val vPrice: Int get() = price!!
    val vIsPublished: Boolean get() = isPublished!!
    val vAuthorIdList: List<Int> get() = authorIdList!!
}