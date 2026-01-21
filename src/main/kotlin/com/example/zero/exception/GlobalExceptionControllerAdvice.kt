package com.example.zero.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ResponseStatusException

/**
 * Используем глобальный обработчик исключений для более читабельного вида ошибок
 */

@ControllerAdvice
class GlobalExceptionControllerAdvice {

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(ex: IllegalStateException): ResponseEntity<ExceptionMessageModel> {
        val error = ExceptionMessageModel(
            HttpStatus.BAD_REQUEST.value(),ex.message
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<ExceptionMessageModel> {
        val error = ExceptionMessageModel(
            HttpStatus.NOT_FOUND.value(), ex.message
        )
        return ResponseEntity(error, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): ResponseEntity<ExceptionMessageModel> {
        val error = ExceptionMessageModel(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.message
        )
        return ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(ParsingException::class)
    fun handleParsingException(ex: ParsingException): ResponseEntity<ExceptionMessageModel> {
        val error = ExceptionMessageModel(
            HttpStatus.BAD_REQUEST.value(), ex.message
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ExceptionMessageModel> {
        val content = ex.bindingResult.fieldErrors
            .map { ValExceptionOutput(it.field, "[${it.defaultMessage}]", it.rejectedValue)}
        val error = ExceptionMessageModel(
            HttpStatus.BAD_REQUEST.value(), content
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(DuplicateException::class)
    fun handleWrongEnum(ex: DuplicateException): ResponseEntity<ExceptionMessageModel> {
        val error = ExceptionMessageModel(
            HttpStatus.BAD_REQUEST.value(), ex.message
        )
        return ResponseEntity(error, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatus(ex: ResponseStatusException): ResponseEntity<ExceptionMessageModel> {
        val error = ExceptionMessageModel(
            ex.statusCode.value(), ex.reason
        )
        return ResponseEntity(error, ex.statusCode)
    }
}
