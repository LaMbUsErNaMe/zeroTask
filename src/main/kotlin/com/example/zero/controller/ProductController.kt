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

interface ProductController{

    fun create(dto: CreateProductRequest): UUID

    fun getAll(pageable: Pageable): Page<ResponseProduct>

    fun getById(id: UUID): ResponseProduct

    fun delete(id: UUID)

    fun update(id: UUID, dto: UpdateProductRequest)

    fun patch(id: UUID, dto: PatchProductRequest)

}
