package com.example.zero.services

import com.example.zero.annotation.MeasureExecTime
import com.example.zero.exception.DuplicateException
import com.example.zero.exception.NotFoundException
import com.example.zero.extension.toProductDto
import com.example.zero.extension.toProductEntity
import com.example.zero.persistence.entity.ProductEntity
import com.example.zero.persistence.repository.ProductRepository
import com.example.zero.services.dto.ProductDto
import com.example.zero.services.dto.CreateProductServiceDto
import com.example.zero.services.dto.PatchProductServiceDto
import com.example.zero.services.dto.UpdateProductServiceDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.math.BigDecimal
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
    private val productRepository: ProductRepository,
    private val jdbcTemplate: JdbcTemplate



): ProductService {

    @Value("\${app.schedule.priceIncreasePercentage}")
    lateinit var priceIncrease: String

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
        existsChekAndGetProduct(id)
        productRepository.deleteById(id)
    }

    @MeasureExecTime
    override fun findById(id: UUID): ProductDto {
        val product = existsChekAndGetProduct(id)
        return product.toProductDto()
    }

    override fun findAll(pageable: Pageable): Page<ProductDto> {
        return productRepository.findAll(pageable).map{ it.toProductDto() }
    }

    @Transactional
    override fun update(id: UUID, dto: UpdateProductServiceDto){
        val product = existsChekAndGetProduct(id)

        if(product.productNumber != dto.productNumber &&
            productRepository.existsByProductNumber(dto.productNumber))
            throw DuplicateException("Такой артикул уже есть в базе!")

        val oldQuantity = product.quantity
        if (oldQuantity != dto.quantity) {
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
        val product = existsChekAndGetProduct(id)

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

    @Transactional
    override fun priceUp() {
        println("SIMPLE SCHEDULER START")
        val products = productRepository.findAll().asSequence()
            .onEach{ it.price = it.price.multiply(BigDecimal(priceIncrease)) }.toList()
        productRepository.saveAll(products)
        println("SIMPLE SCHEDULER END")
    }

    @Transactional
    override fun priceUpOpt() {
        var inc = 1
        val log = ArrayList<String>()
        val file = File("opt_sheluder_log.txt")
        println("OPT SCHEDULER START")
        jdbcTemplate.query("""SELECT id, price FROM product_schema.products FOR UPDATE""")
        { resultSet ->
            val id: UUID = resultSet.getObject("id", UUID::class.java)
            val price = resultSet.getBigDecimal("price")
            val newPrice = price.multiply(BigDecimal(priceIncrease))

            jdbcTemplate.update("UPDATE product_schema.products SET price = ? WHERE id = ?", newPrice, id)
            log.add(String.format("%07d", inc) + " : ID : $id OLD : $price NEW : $newPrice")
            inc++
        }
        try {
            val fileContent = log.joinToString(separator = "\n")

            file.writeText(fileContent)
            println("Successfully wrote log")

        } catch (e: Exception) {
            e.printStackTrace()
        }

        log.clear()

        println("OPT SCHEDULER END")
    }

    override fun existsChekAndGetProduct(id: UUID): ProductEntity {
        return productRepository.findByIdOrNull(id) ?: throw NotFoundException("Товар [$id] не найден!")
    }
}
