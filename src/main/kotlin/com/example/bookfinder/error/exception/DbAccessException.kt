package com.example.bookfinder.error.exception

/**
 * Custom Exception for error while database connecting.
 */
class DbAccessException : RuntimeException {
    constructor(message: String) : super(message)
}