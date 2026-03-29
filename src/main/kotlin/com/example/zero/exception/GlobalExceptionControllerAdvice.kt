package com.example.zero.exception

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

/**
 * Используем глобальный обработчик исключений для более читабельного вида ошибок
 */
@RestControllerAdvice
class GlobalExceptionControllerAdvice {

    @ExceptionHandler(RemoteServiceException::class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    fun handleRemoteService(ex: RemoteServiceException): ExceptionMessageModel =
        handleErrorResponse(
            status = HttpStatus.BAD_GATEWAY,
            message = "External service error: ${ex.message}",
            ex = ex
        )

    @ExceptionHandler(EmptyResponseException::class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    fun handleEmptyResponse(ex: EmptyResponseException): ExceptionMessageModel =
        handleErrorResponse(
            status = HttpStatus.BAD_GATEWAY,
            message = "External service returned empty response: ${ex.message}",
            ex = ex
        )

    @ExceptionHandler(InvalidResponseException::class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    fun handleInvalidResponse(ex: InvalidResponseException): ExceptionMessageModel =
        handleErrorResponse(
            status = HttpStatus.BAD_GATEWAY,
            message = "External service returned invalid data: ${ex.message}",
            ex = ex
        )

    @ExceptionHandler(IntegrationException::class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    fun handleIntegration(ex: IntegrationException): ExceptionMessageModel =
        handleErrorResponse(
            status = HttpStatus.BAD_GATEWAY,
            message = ex.message ?: "Integration error",
            ex = ex
        )

    @ExceptionHandler(IllegalStateException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalState(ex: IllegalStateException): ExceptionMessageModel =
        handleErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = ex.message,
            ex = ex
        )

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(ex: NotFoundException): ExceptionMessageModel =
        handleErrorResponse(
            status = HttpStatus.NOT_FOUND,
            message = ex.message,
            ex = ex
        )

    @ExceptionHandler(AccessForbidden::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAccessForbidden(ex: AccessForbidden): ExceptionMessageModel =
        handleErrorResponse(
            status = HttpStatus.FORBIDDEN,
            message = ex.message,
            ex = ex
        )

    @ExceptionHandler(ParsingException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleParsingException(ex: ParsingException): ExceptionMessageModel =
        handleErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = ex.message,
            ex = ex
        )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(ex: MethodArgumentNotValidException): ExceptionMessageModel {
        val content = ex.bindingResult.fieldErrors.map {
            ValExceptionOutput(
                it.field,
                "[${it.defaultMessage}]",
                it.rejectedValue
            )
        }

        return handleErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = content,
            ex = ex
        )
    }

    @ExceptionHandler(DuplicateException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleDuplicateException(ex: DuplicateException): ExceptionMessageModel =
        handleErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = ex.message,
            ex = ex
        )

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatus(ex: ResponseStatusException): ExceptionMessageModel =
        handleErrorResponse(
            status = HttpStatus.valueOf(ex.statusCode.value()),
            message = ex.reason,
            ex = ex
        )

    @ExceptionHandler(RuntimeException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleRuntimeException(ex: RuntimeException): ExceptionMessageModel =
        handleErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            message = ex.message ?: "Internal server error",
            ex = ex
        )

    private fun handleErrorResponse(
        status: HttpStatus,
        message: Any?,
        ex: Throwable
    ): ExceptionMessageModel {
        val error = ExceptionMessageModel(
            status = status.value(),
            message = message
        )

        logger.error(ex) {
            "Handled ${ex.javaClass.simpleName}: status=${error.status}, message=${error.message}"
        }

        return error
    }

    private companion object {
        val logger = KotlinLogging.logger {}
    }
}
