package com.example.bookfinder.controller

import com.example.bookfinder.model.BookModel
import com.example.bookfinder.service.BookService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
class BookController(private val bookService: BookService) {

    /**
     * Get the books by author.
     *
     * @param keyword author name.
     */
    @GetMapping("/book")
    fun get(@RequestParam("keyword") @NotBlank(message = "{NotNull.book.id}") keyword: String): List<BookModel> {
        return bookService.get(keyword)
    }

    /**
     * Register the book.
     *
     * @param request book model to register.
     */
    @PostMapping("/book")
    fun register(@Valid @RequestBody request: BookModel): BookModel {
        return bookService.register(request)
    }

    /**
     * Update the book.
     *
     * @param request book model to update.
     */
    @PutMapping("/book")
    fun update(@Valid @RequestBody request: BookModel): BookModel {
        return bookService.update(request)
    }
}