package com.example.bookfinder.error.response

import com.example.bookfinder.error.response.ErrorResponse
import org.springframework.http.HttpStatus

class InternalServerErrorResponse (
    path: String,
    message: String
) : ErrorResponse(
    status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
    error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
    message = message,
    path = path
)