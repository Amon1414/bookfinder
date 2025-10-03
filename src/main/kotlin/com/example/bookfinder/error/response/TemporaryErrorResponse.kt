package com.example.bookfinder.error.response

import com.example.bookfinder.error.response.ErrorResponse
import org.springframework.http.HttpStatus

class TemporaryErrorResponse (
    path: String,
    message: String
) : ErrorResponse(
    status = HttpStatus.SERVICE_UNAVAILABLE.value(),
    error = HttpStatus.SERVICE_UNAVAILABLE.reasonPhrase,
    message = message,
    path = path
)