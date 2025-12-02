package com.example.zero.controller

import com.example.zero.dto.request.ProductRequestDto
import com.example.zero.dto.request.patch.ProductPatchRequestDto
import com.example.zero.dto.request.update.ProductUpdateRequestDto
import com.example.zero.dto.response.ProductResponseDto
import com.example.zero.services.ProductService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

import javax.validation.Valid

/**
 * @Valid - проверяет на все ограничения указанные в Dto
 *
 * Контроллер - слой представления.
 *
 * Аннотация @RestController говорит спрингу что это контролер но уже с response body.
 *
 * аннотации POST GET DELETE PUT PATCH + Mapping говорят какой HTTP метод ожидать
 *
 * create -  ринимаем нужный dto, валидируем отсдаём сервису
 *
 * delete - ринимаем нужный dto, валидируем отсдаём сервису
 *
 * getById - ринимаем нужный dto, валидируем отсдаём сервису
 *
 * getAll - ринимаем нужный dto, валидируем отсдаём сервису
 *
 * update - ринимаем нужный dto, валидируем отсдаём сервису
 *
 * patch - ринимаем нужный dto, валидируем отсдаём сервису
 */

@RestController
@RequestMapping("/products")

class ProductController(
    private val productService: ProductService
) {

    @Operation(summary = "Добавить товар")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody dto: ProductRequestDto): ProductResponseDto {
        return productService.save(dto)
    }
    @Operation(summary = "Получить все товары")
    @GetMapping
    fun getAll(pageable: Pageable): Page<ProductResponseDto> =
        productService.findAll(pageable)

    @Operation(summary = "Получить товар по идентификатору")
    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ProductResponseDto =
        productService.findById(id)


    @Operation(summary = "Удаление товара по идентификатору")
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID) {
        productService.deleteById(id)
    }

    @Operation(summary = "Изменение всех полей")
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID, @Valid @RequestBody dto: ProductUpdateRequestDto
    ): ProductResponseDto = productService.update(id, dto)


    @Operation(summary = "Изменение части полей")
    @PatchMapping("/{id}")
    fun patch(
        @PathVariable id: UUID,
        @Valid @RequestBody dto: ProductPatchRequestDto
    ): ProductResponseDto = productService.patch(id, dto)

}