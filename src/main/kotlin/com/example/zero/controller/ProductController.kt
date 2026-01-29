package com.example.zero.controller

import com.example.zero.controller.dto.product.request.CreateProductRequest
import com.example.zero.controller.dto.product.request.patch.PatchProductRequest
import com.example.zero.controller.dto.product.request.search.SearchDto
import com.example.zero.controller.dto.product.request.search.SearchFilterDto
import com.example.zero.controller.dto.product.request.update.UpdateProductRequest
import com.example.zero.controller.dto.product.response.ResponseProduct
import com.example.zero.services.dto.product.ProductDto
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

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

    fun search(searchRequest: SearchDto, pageable: Pageable): Page<ProductDto>

}
