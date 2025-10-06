package com.example.bookfinder.handler

import com.example.bookfinder.constants.ErrorMessages
import com.example.bookfinder.error.exception.DbAccessException
import com.example.bookfinder.error.exception.InvalidOperationException
import com.example.bookfinder.error.exception.ServiceUnavailableException
import com.example.bookfinder.error.response.BadRequestErrorResponse
import com.example.bookfinder.error.response.InternalServerErrorResponse
import com.example.bookfinder.error.response.TemporaryErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import io.mockk.every
import io.mockk.mockk
import org.springframework.core.MethodParameter
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.FieldError

class GlobalExceptionHandlerTest {

    private lateinit var handler: GlobalExceptionHandler
    private lateinit var request: HttpServletRequest

    @BeforeEach
    fun setup() {
        handler = GlobalExceptionHandler()
        request = mockk()
        every { request.requestURI } returns "/test/api"
    }

    @Test
    fun `handleInvalidField should return BadRequestErrorResponse`() {
        val bindingResult = BeanPropertyBindingResult(Any(), "test")
        bindingResult.addError(FieldError("test", "name", "Invalid name"))
        val methodParameter = mockk<MethodParameter>(relaxed = true)
        val ex = MethodArgumentNotValidException(methodParameter, bindingResult)

        val response = handler.handleInvalidField(ex, request)

        assertEquals(400, response.statusCode.value())
        val body = response.body as BadRequestErrorResponse
        assertEquals("Invalid name", body.message)
        assertEquals("/test/api", body.path)
    }

    @Test
    fun `handleInvalidParameter should return BadRequestErrorResponse`() {
        val ex = ConstraintViolationException("Invalid param", emptySet())
        val response = handler.handleInvalidParameter(ex, request)

        assertEquals(400, response.statusCode.value())
        val body = response.body as BadRequestErrorResponse
        assertEquals("Invalid param", body.message)
        assertEquals("/test/api", body.path)
    }

    @Test
    fun `handleInvalidOperation should return BadRequestErrorResponse`() {
        val ex = InvalidOperationException("Invalid operation")
        val response = handler.handleInvalidOperation(ex, request)

        assertEquals(400, response.statusCode.value())
        val body = response.body as BadRequestErrorResponse
        assertEquals("Invalid operation", body.message)
        assertEquals("/test/api", body.path)
    }

    @Test
    fun `handleDatabaseError should return InternalServerErrorResponse`() {
        val ex = DbAccessException("DB error")
        val response = handler.handleDatabaseError(ex, request)

        assertEquals(500, response.statusCode.value())
        val body = response.body as InternalServerErrorResponse
        assertEquals("DB error", body.message)
        assertEquals("/test/api", body.path)
    }

    @Test
    fun `handleTemporaryError should return TemporaryErrorResponse`() {
        val ex = ServiceUnavailableException("Temporary error")
        val response = handler.handleTemporaryError(ex, request)

        assertEquals(503, response.statusCode.value())
        val body = response.body as TemporaryErrorResponse
        assertEquals("Temporary error", body.message)
        assertEquals("/test/api", body.path)
    }

    @Test
    fun `handleInvalidField should fallback to default message when no field error`() {
        val bindingResult = BeanPropertyBindingResult(Any(), "test")
        val methodParameter = mockk<MethodParameter>(relaxed = true)
        val ex = MethodArgumentNotValidException(methodParameter, bindingResult)

        val response = handler.handleInvalidField(ex, request)

        assertEquals(400, response.statusCode.value())
        val body = response.body as BadRequestErrorResponse
        assertEquals(ErrorMessages.INVALID_FIELD, body.message)
    }

    @Test
    fun `handleInvalidParameter should fallback to default message when message is null`() {
        val ex = ConstraintViolationException(null, emptySet())
        val response = handler.handleInvalidParameter(ex, request)

        assertEquals(400, response.statusCode.value())
        val body = response.body as BadRequestErrorResponse
        assertEquals(ErrorMessages.INVALID_PARAMETER, body.message)
    }

    @Test
    fun `handleInvalidOperation should fallback to default message when message is null`() {
        val ex = InvalidOperationException(null)
        val response = handler.handleInvalidOperation(ex, request)

        assertEquals(400, response.statusCode.value())
        val body = response.body as BadRequestErrorResponse
        assertEquals(ErrorMessages.INVALID_OPERATION, body.message)
    }

    @Test
    fun `handleDatabaseError should fallback to default message when message is null`() {
        val ex = DbAccessException(null)
        val response = handler.handleDatabaseError(ex, request)

        assertEquals(500, response.statusCode.value())
        val body = response.body as InternalServerErrorResponse
        assertEquals(ErrorMessages.INTERNAL_SERVER_ERROR, body.message)
    }

    @Test
    fun `handleTemporaryError should fallback to default message when message is null`() {
        val ex = ServiceUnavailableException(null)
        val response = handler.handleTemporaryError(ex, request)

        assertEquals(503, response.statusCode.value())
        val body = response.body as TemporaryErrorResponse
        assertEquals(ErrorMessages.TEMPORARY_ERROR, body.message)
    }
}
