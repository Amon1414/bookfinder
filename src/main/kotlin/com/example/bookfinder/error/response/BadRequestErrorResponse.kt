package com.example.bookfinder.error.response

import com.example.bookfinder.error.response.ErrorResponse
import org.springframework.http.HttpStatus

class BadRequestErrorResponse(
    message: String,
    path: String
) : ErrorResponse(
    status = HttpStatus.BAD_REQUEST.value(),
    error = HttpStatus.BAD_REQUEST.reasonPhrase,
    message = message,
    path = path
)