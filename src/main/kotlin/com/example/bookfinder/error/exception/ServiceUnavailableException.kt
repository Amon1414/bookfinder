package com.example.bookfinder.error.exception

/**
 * Custom Exception for temporary server error.
 */
class ServiceUnavailableException: RuntimeException {
    constructor(message: String) : super(message)
}