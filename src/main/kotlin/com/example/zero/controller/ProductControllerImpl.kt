package com.example.zero.controller

import com.example.zero.controller.dto.request.CreateProductRequest
import com.example.zero.controller.dto.request.patch.PatchProductRequest
import com.example.zero.controller.dto.request.update.UpdateProductRequest
import com.example.zero.controller.dto.response.ResponseProduct
import com.example.zero.extension.toCreateProductServiceDto
import com.example.zero.extension.toPatchProductServiceDto
import com.example.zero.extension.toProductResponseDto
import com.example.zero.extension.toUpdateProductServiceDto
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

class ProductControllerImpl(
    private val productService: ProductService
) : ProductController {

    @Operation(summary = "Добавить товар")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    override fun create(@Valid @RequestBody dto: CreateProductRequest): UUID {
        return productService.save(dto.toCreateProductServiceDto())
    }

    @Operation(summary = "Получить все товары")
    @GetMapping
    override fun getAll(pageable: Pageable): Page<ResponseProduct> {
        return  productService.findAll(pageable).map { it.toProductResponseDto() }
    }

    @Operation(summary = "Получить товар по идентификатору")
    @GetMapping("/{id}")
    override fun getById(@PathVariable id: UUID): ResponseProduct {
        return productService.findById(id).toProductResponseDto()
    }

    @Operation(summary = "Удаление товара по идентификатору")
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun delete(@PathVariable id: UUID) {
        productService.deleteById(id)
    }

    @Operation(summary = "Изменение всех полей")
    @PutMapping("/{id}")
    override fun update(
        @PathVariable id: UUID, @Valid @RequestBody dto: UpdateProductRequest
    ){
        return productService.update(id, dto.toUpdateProductServiceDto())
    }

    @Operation(summary = "Изменение части полей")
    @PatchMapping("/{id}")
    override fun patch(
        @PathVariable id: UUID,
        @Valid @RequestBody dto: PatchProductRequest
    ){
        return productService.patch(id, dto.toPatchProductServiceDto())
    }

}
