package com.example.bookfinder.error.response

import java.time.LocalDateTime

open class ErrorResponse (
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)