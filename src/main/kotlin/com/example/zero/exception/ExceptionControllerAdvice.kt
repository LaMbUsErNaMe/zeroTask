package com.example.zero.exception

import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ResponseStatusException

/**
 * Используем глобальный обработчик исключений для более читабельного вида ошибок
 */

@ControllerAdvice
class ExceptionControllerAdvice {

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(ex: IllegalStateException): ResponseEntity<ErrorMessageModel> {
        val error = ErrorMessageModel(
            HttpStatus.BAD_REQUEST.value(),ex.message
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<ErrorMessageModel> {
        val error = ErrorMessageModel(
            HttpStatus.NOT_FOUND.value(), ex.message
        )
        return ResponseEntity(error, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(WrongEnumException::class)
    fun handleWrongEnum(ex: WrongEnumException): ResponseEntity<ErrorMessageModel> {
        val error = ErrorMessageModel(
            HttpStatus.BAD_REQUEST.value(), ex.message
        )
        return ResponseEntity(error, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatus(ex: ResponseStatusException): ResponseEntity<ErrorMessageModel> {
        val error = ErrorMessageModel(
            ex.statusCode.value(), ex.reason
        )
        return ResponseEntity(error, ex.statusCode)
    }
}
