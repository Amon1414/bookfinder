package com.example.bookfinder.handler

import com.example.bookfinder.constants.ErrorMessages
import com.example.bookfinder.error.exception.DbAccessException
import com.example.bookfinder.error.exception.InvalidOperationException
import com.example.bookfinder.error.exception.ServiceUnavailableException
import com.example.bookfinder.error.response.BadRequestErrorResponse
import com.example.bookfinder.error.response.ErrorResponse
import com.example.bookfinder.error.response.InternalServerErrorResponse
import com.example.bookfinder.error.response.TemporaryErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    /**
     * Handle an error of invalid request field.
     *
     * @param ex MethodArgumentNotValidException.
     * @param request http servlet request.
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleInvalidField(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = BadRequestErrorResponse(
            message = ex.bindingResult.fieldError?.defaultMessage ?: ErrorMessages.INVALID_FIELD,
            path = request.requestURI
        )
        return ResponseEntity.status(errorResponse.status).body(errorResponse)
    }

    /**
     * Handle an error of invalid request parameter.
     *
     * @param ex ConstraintViolationException.
     * @param request http servlet request.
     */
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleInvalidParameter(
        ex: ConstraintViolationException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = BadRequestErrorResponse(
            message = ex.message ?: ErrorMessages.INVALID_PARAMETER,
            path = request.requestURI
        )
        return ResponseEntity.status(errorResponse.status).body(errorResponse)
    }

    /**
     * Handle an error of invalid user operation.
     *
     * @param ex InvalidOperationException.
     * @param request http servlet request.
     */
    @ExceptionHandler(InvalidOperationException::class)
    fun handleInvalidOperation(
        ex: InvalidOperationException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = BadRequestErrorResponse(
            message = ex.message ?: ErrorMessages.INVALID_OPERATION,
            path = request.requestURI
        )
        return ResponseEntity.status(errorResponse.status).body(errorResponse)
    }

    /**
     * Handle an error of database.
     *
     * @param ex DbAccessException.
     * @param request http servlet request.
     */
    @ExceptionHandler(DbAccessException::class)
    fun handleDatabaseError(
        ex: DbAccessException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = InternalServerErrorResponse(
            message = ex.message ?: ErrorMessages.INTERNAL_SERVER_ERROR,
            path = request.requestURI
        )
        return ResponseEntity.status(errorResponse.status).body(errorResponse)
    }

    /**
     * Handle a temporary error.
     *
     * @param ex ServiceUnavailableException.
     * @param request http servlet request.
     */
    @ExceptionHandler(ServiceUnavailableException::class)
    fun handleTemporaryError(
        ex: ServiceUnavailableException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = TemporaryErrorResponse(
            message = ex.message ?: ErrorMessages.TEMPORARY_ERROR,
            path = request.requestURI
        )
        return ResponseEntity.status(errorResponse.status).body(errorResponse)
    }
}