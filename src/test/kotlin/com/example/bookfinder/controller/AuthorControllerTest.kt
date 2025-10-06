package com.example.bookfinder.controller

import com.example.bookfinder.model.AuthorModel
import com.example.bookfinder.service.AuthorService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@WebMvcTest(AuthorController::class)
class AuthorControllerTest @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper
) {

    @MockkBean
    lateinit var authorService: AuthorService

    @Test
    fun `register should return registered author`() {
        val request = AuthorModel(id = 1, name = "John Doe", birthDate = LocalDate.of(1970, 1, 1))
        val response = AuthorModel(id = 1, name = "John Doe", birthDate = LocalDate.of(1970, 1, 1))
        every { authorService.register(any()) } returns response

        mockMvc.perform(
            post("/author")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("John Doe"))
            .andExpect(jsonPath("$.birthDate").value("1970-01-01"))
    }

    @Test
    fun `passing invalid values will cause an error`() {
        val request = AuthorModel(id = 1, name = "", birthDate = LocalDate.of(1970, 1, 1))
        val response = AuthorModel(id = 1, name = "John Doe", birthDate = LocalDate.of(1970, 1, 1))
        every { authorService.register(any()) } returns response

        mockMvc.perform(
            post("/author")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("John Doe"))
            .andExpect(jsonPath("$.birthDate").value("1970-01-01"))
    }

    @Test
    fun `update should return updated author`() {
        val request = AuthorModel(id = 2, name = "Jane Doe", birthDate = LocalDate.of(1970, 1, 1))
        val response = AuthorModel(id = 2, name = "Jane Doe", birthDate = LocalDate.of(1970, 1, 1))
        every { authorService.update(request) } returns response

        mockMvc.perform(
            put("/author")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.name").value("Jane Doe"))
            .andExpect(jsonPath("$.birthDate").value("1970-01-01"))
    }
}
