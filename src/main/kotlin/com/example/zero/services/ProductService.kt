package com.example.zero.services

import com.example.zero.dto.request.ProductRequestDto
import com.example.zero.dto.request.patch.ProductPatchRequestDto
import com.example.zero.dto.request.update.ProductUpdateRequestDto
import com.example.zero.dto.response.ProductResponseDto
import com.example.zero.exception.DuplicateException
import com.example.zero.exception.NotFoundException
import com.example.zero.mapper.ProductMapper
import com.example.zero.repository.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

/**
 * Сервис - слой бизнес логики. Те же CRUD которые вызывает контроллер отсюда
 * а сами эти функции вызывают одноимённые
 * но уже в репозитории для работы со слоем данных.
 *
 * Аннотация @Service говорит спрингу что это сервис.
 *
 * save - Ф-ция сохранения, получаем Dto маппим в энтити, в репозитории сохраняем.
 * Возвращаем ответ в ввиде Dto.
 *
 * deleteById - Ф-ция удаления, получаем UUID ищем и удаляем,
 * если не нашли то шлём кастомное исключение.
 *
 * findById - Ф-ция получения товара, получаем UUID ищем и возвращаем ответный Dto,
 * если не нашли то шлём кастомное исключение.
 *
 * findAll - Ф-ция получения списка всех товаров, возвращаем СТРАНИЦУ респонсе Dto,
 *
 * update - Ф-ция обновления всех полей товара, получаем UUID ищем, находим в репо
 * сеттим поля ентити на dto-шные, возвращаем ответный Dto,
 * если не нашли то шлём кастомное исключение.
 * !!Проверка на изменение кол-ва товаров тут. Если поменялосб,
 * то меняем и время измененияя
 *
 * patch - тоже самое но обновляем лишь некоторые поля
 */

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val productMapper: ProductMapper
) {

    fun save(dto: ProductRequestDto): ProductResponseDto {
        if(productRepository.existsByProductNumber(dto.productNumber))
            throw DuplicateException("Такой артикул уже есть в базе!")
        val entity = productMapper.toEntity(dto)
        val saved = productRepository.save(entity)
        return productMapper.toDto(saved)
    }

    fun deleteById(id: UUID) {
        if (!productRepository.existsById(id)) {
            throw NotFoundException("Товар [$id] для удаления не найден");
        }
        productRepository.deleteById(id);
    }

    fun findById(id: UUID): ProductResponseDto {
        val product = productRepository.findById(id)
            .orElseThrow { NotFoundException("Товар [$id] не найден") }
        return productMapper.toDto(product)
    }

    fun findAll(pageable: Pageable): Page<ProductResponseDto> {
        return productRepository.findAll(pageable).map(productMapper::toDto);
    }

    fun update(id: UUID, dto: ProductUpdateRequestDto): ProductResponseDto {
        val product = productRepository.findById(id)
            .orElseThrow { NotFoundException("Товар [$id] для изменения не найден") }
        val oldQVal = product.quantity

        product.name = dto.name
        product.description = dto.description
        product.price = dto.price
        product.quantity = dto.quantity

        if (oldQVal != dto.quantity) {
            product.timeStampQuantityChanged = LocalDateTime.now()
        }

        val updated = productRepository.save(product)
        return productMapper.toDto(updated)
    }

    fun patch(id: UUID, dto: ProductPatchRequestDto): ProductResponseDto {
        val product = productRepository.findById(id)
            .orElseThrow { NotFoundException("Товар [$id] для изменения не найден") }

        val oldQuantity = product.quantity

        dto.name?.let { product.name = it }
        dto.description?.let { product.description = it }
        dto.category?.let { product.category = it }
        dto.price?.let { product.price = it }
        dto.quantity?.let {
            product.quantity = it
            if (oldQuantity != it) {
                product.timeStampQuantityChanged = LocalDateTime.now()
            }
        }

        val updated = productRepository.save(product)
        return productMapper.toDto(updated)
    }


}
