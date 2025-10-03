package com.example.bookfinder.service

import com.example.bookfinder.model.AuthorModel
import com.example.bookfinder.repository.AuthorRepository
import org.springframework.stereotype.Service

@Service
class AuthorService(private val authorRepository: AuthorRepository) {

    /**
     * Register the author.
     *
     * @param author author model to request.
     */
    fun register(author: AuthorModel): AuthorModel {
        return authorRepository.register(author)
    }

    /**
     * Update the author.
     *
     * @param author author model to update.
     */
    fun update(author: AuthorModel): AuthorModel {
        return authorRepository.update(author)
    }
}