package com.example.zero.services

import com.example.zero.exception.DuplicateException
import com.example.zero.exception.NotFoundException
import com.example.zero.extension.toProductDto
import com.example.zero.extension.toProductEntity
import com.example.zero.persistence.repository.ProductRepository
import com.example.zero.services.dto.ProductDto
import com.example.zero.services.dto.CreateProductServiceDto
import com.example.zero.services.dto.PatchProductServiceDto
import com.example.zero.services.dto.UpdateProductServiceDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

/**
 * Сервис - слой бизнес логики. Те же CRUD которые вызывает контроллер отсюда,
 * а сами эти функции вызывают одноимённые,
 * но уже в репозитории для работы со слоем данных.
 *
 * Аннотация @Service говорит спрингу что это сервис.
 *
 * Save - Ф-ция сохранения, получаем Dto маппим в энтити, в репозитории сохраняем.
 * Возвращаем ответ ввиде Dto.
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
 * !! Проверка на изменение кол-ва товаров тут. Если поменялосб,
 * то меняем и время измененияя
 *
 * patch - то же самое, но обновляем лишь некоторые поля
 */

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository


): ProductService {
    @Transactional
    override fun save(dto: CreateProductServiceDto): UUID {
        if(productRepository.existsByProductNumber(dto.productNumber)){
            throw DuplicateException("Такой артикул уже есть в базе!")
        }
        val saved = productRepository.save(dto.toProductEntity())
        return saved.id!!
    }
    @Transactional
    override fun deleteById(id: UUID) {
        if (!productRepository.existsById(id)) {
            throw NotFoundException("Товар [$id] для удаления не найден")
        }
        productRepository.deleteById(id);
    }

    override fun findById(id: UUID): ProductDto {

        val product = productRepository.findByIdOrNull(id)
            ?: throw NotFoundException("Товар [$id] не найден")
        return product.toProductDto()
    }

    override fun findAll(pageable: Pageable): Page<ProductDto> {
        return productRepository.findAll(pageable).map{ it.toProductDto() };
    }

    @Transactional
    override fun update(id: UUID, dto: UpdateProductServiceDto){ //
        val product = productRepository.findById(id)
            .orElseThrow { NotFoundException("Товар [$id] для изменения не найден") }

        if(product.productNumber != dto.productNumber &&
            productRepository.existsByProductNumber(dto.productNumber))
            throw DuplicateException("Такой артикул уже есть в базе!")

        val oldQVal = product.quantity
        if (oldQVal != dto.quantity) {
            product.quantityChangedDateTime = LocalDateTime.now()
        }

        product.productNumber = dto.productNumber
        product.name = dto.name
        product.description = dto.description
        product.price = dto.price
        product.quantity = dto.quantity

        productRepository.save(product)
    }

    @Transactional
    override fun patch(id: UUID, dto: PatchProductServiceDto) {
        val product = productRepository.findById(id)
            .orElseThrow { NotFoundException("Товар [$id] для изменения не найден") }

        if(dto.productNumber != null && product.productNumber != dto.productNumber &&
            productRepository.existsByProductNumber(dto.productNumber))
            throw DuplicateException("Такой артикул уже есть в базе!")

        val oldQuantity = product.quantity

        dto.name?.let { product.name = it }
        dto.productNumber?.let { product.productNumber = it }
        dto.description?.let { product.description = it }
        dto.categoryType?.let { product.categoryType = it }
        dto.price?.let { product.price = it }
        dto.quantity?.let {
            product.quantity = it
            if (oldQuantity != it) {
                product.quantityChangedDateTime = LocalDateTime.now()
            }
        }
        productRepository.save(product)
    }
}
