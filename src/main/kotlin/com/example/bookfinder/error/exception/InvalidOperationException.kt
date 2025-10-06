package com.example.bookfinder.error.exception

/**
 * Custom Exception for invalid user operation.
 */
class InvalidOperationException : RuntimeException {
    constructor(message: String?) : super(message)
}