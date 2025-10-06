package com.example.bookfinder.controller

import com.example.bookfinder.model.BookModel
import com.example.bookfinder.service.BookService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(BookController::class)
class BookControllerTest @Autowired constructor (
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper
) {

    @MockkBean
    lateinit var bookService: BookService

    @Test
    fun `get should return books when request parameter is valid`() {
        val books = listOf(
            BookModel(id = 1, title = "Spring Boot", price = 2500, isPublished = true, authorIdList = listOf(1)),
            BookModel(id = 2, title = "Kotlin Master", price = 5000, isPublished = false, authorIdList = listOf(1))
        )
        every { bookService.get(1) } returns books

        mockMvc.perform(get("/book")
            .param("authorId", "1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].title").value("Spring Boot"))
            .andExpect(jsonPath("$[0].price").value(2500))
            .andExpect(jsonPath("$[0].isPublished").value(true))
            .andExpect(jsonPath("$[0].authorIdList[0]").value(1))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].title").value("Kotlin Master"))
            .andExpect(jsonPath("$[1].price").value(5000))
            .andExpect(jsonPath("$[1].isPublished").value(false))
            .andExpect(jsonPath("$[1].authorIdList[0]").value(1))
    }

    @Test
    fun `register should return registered book`() {
        val request = BookModel(id = 1, title = "Spring Boot", price = 2500, isPublished = true, authorIdList = listOf(1))
        val response = BookModel(id = 1, title = "Spring Boot", price = 2500, isPublished = true, authorIdList = listOf(1))
        every { bookService.register(request) } returns response

        mockMvc.perform(post("/book")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Spring Boot"))
            .andExpect(jsonPath("$.price").value(2500))
            .andExpect(jsonPath("$.isPublished").value(true))
            .andExpect(jsonPath("$.authorIdList[0]").value(1))
    }

    @Test
    fun `update should return updated book`() {
        val request = BookModel(id = 1, title = "Spring Boot", price = 2500, isPublished = true, authorIdList = listOf(1))
        val response = BookModel(id = 1, title = "Spring Boot", price = 2500, isPublished = true, authorIdList = listOf(1))
        every { bookService.update(request) } returns response

        mockMvc.perform(put("/book")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Spring Boot"))
            .andExpect(jsonPath("$.price").value(2500))
            .andExpect(jsonPath("$.isPublished").value(true))
            .andExpect(jsonPath("$.authorIdList[0]").value(1))
    }

    @Test
    fun `get should return 400 when author id is null`() {
        mockMvc.perform(get("/book")
            .param("authorId", null))
            .andExpect(status().isBadRequest)
    }
}
