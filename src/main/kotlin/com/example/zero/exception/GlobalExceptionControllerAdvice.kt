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

    @ExceptionHandler(RemoteServiceException::class)
    fun handleRemoteService(ex: RemoteServiceException): ResponseEntity<ExceptionMessageModel> {
        val response = ExceptionMessageModel(
            status = HttpStatus.BAD_GATEWAY.value(),
            message = "External service error: ${ex.message}"
        )
        return ResponseEntity(response, HttpStatus.BAD_GATEWAY)
    }

    @ExceptionHandler(EmptyResponseException::class)
    fun handleEmptyResponse(ex: EmptyResponseException): ResponseEntity<ExceptionMessageModel> {
        val response = ExceptionMessageModel(
            status = HttpStatus.BAD_GATEWAY.value(),
            message = "External service returned empty response: ${ex.message}"
        )
        return ResponseEntity(response, HttpStatus.BAD_GATEWAY)
    }

    @ExceptionHandler(InvalidResponseException::class)
    fun handleInvalidResponse(ex: InvalidResponseException): ResponseEntity<ExceptionMessageModel> {
        val response = ExceptionMessageModel(
            status = HttpStatus.BAD_GATEWAY.value(),
            message = "External service returned invalid data: ${ex.message}"
        )
        return ResponseEntity(response, HttpStatus.BAD_GATEWAY)
    }

    @ExceptionHandler(IntegrationException::class)
    fun handleIntegration(ex: IntegrationException): ResponseEntity<ExceptionMessageModel> {
        val response = ExceptionMessageModel(
            status = HttpStatus.BAD_GATEWAY.value(),
            message = "Integration error"
        )
        return ResponseEntity(response, HttpStatus.BAD_GATEWAY)
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(ex: IllegalStateException): ResponseEntity<ExceptionMessageModel> {
        val error = ExceptionMessageModel(
            status = HttpStatus.BAD_REQUEST.value(),
            message = ex.message
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<ExceptionMessageModel> {
        val error = ExceptionMessageModel(
            status = HttpStatus.NOT_FOUND.value(),
            message = ex.message
        )
        return ResponseEntity(error, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): ResponseEntity<ExceptionMessageModel> {
        val error = ExceptionMessageModel(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            message = ex.message
        )
        return ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(AccessForbidden::class)
    fun handleAccessForbidden(ex: AccessForbidden): ResponseEntity<ExceptionMessageModel> {
        val error = ExceptionMessageModel(
            status = HttpStatus.FORBIDDEN.value(),
            message = ex.message
        )
        return ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(ParsingException::class)
    fun handleParsingException(ex: ParsingException): ResponseEntity<ExceptionMessageModel> {
        val error = ExceptionMessageModel(
            status = HttpStatus.BAD_REQUEST.value(),
            message = ex.message
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ExceptionMessageModel> {
        val content = ex.bindingResult.fieldErrors
            .map { ValExceptionOutput(it.field, "[${it.defaultMessage}]", it.rejectedValue)}
        val error = ExceptionMessageModel(
            status = HttpStatus.BAD_REQUEST.value(),
            message = content
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(DuplicateException::class)
    fun handleWrongEnum(ex: DuplicateException): ResponseEntity<ExceptionMessageModel> {
        val error = ExceptionMessageModel(
            status = HttpStatus.BAD_REQUEST.value(),
            message = ex.message
        )
        return ResponseEntity(error, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatus(ex: ResponseStatusException): ResponseEntity<ExceptionMessageModel> {
        val error = ExceptionMessageModel(
            status = ex.statusCode.value(),
            message = ex.reason
        )
        return ResponseEntity(error, ex.statusCode)
    }
}
