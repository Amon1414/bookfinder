package com.example.bookfinder.controller

import com.example.bookfinder.model.AuthorModel
import com.example.bookfinder.service.AuthorService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthorController(private val authorService: AuthorService) {

    /**
     * Register the author.
     *
     * @param request author model to request.
     */
    @PostMapping("/author")
    fun register(@RequestBody request: AuthorModel): AuthorModel {
        return authorService.register(request)
    }

    /**
     * Update the author.
     *
     * @param request author model to update.
     */
    @PutMapping("/author")
    fun update(@RequestBody request: AuthorModel): AuthorModel {
        return authorService.update(request)
    }
}