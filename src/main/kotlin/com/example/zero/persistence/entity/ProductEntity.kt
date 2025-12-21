package com.example.zero.persistence.entity

import com.example.zero.enums.CategoryType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@EntityListeners(AuditingEntityListener::class)
@Entity
@Table(name = "products")
class ProductEntity(
    @Id @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null,
    @Column(name = "name", updatable = true, nullable = false)
    var name: String,
    @Column(name = "product_number", unique = true, updatable = true, nullable = false)
    var productNumber: Long,
    @Column(name = "description", updatable = true, nullable = true)
    var description: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "category_type", updatable = true, nullable = false)
    var categoryType: CategoryType,
    @Column(name = "price", updatable = true, nullable = false)
    var price: BigDecimal,
    @Column(name = "quantity", updatable = true, nullable = false)
    var quantity: BigDecimal,
    @LastModifiedDate
    @Column(name = "quantity_changed_date_time", updatable = true, nullable = false)
    var quantityChangedDateTime: LocalDateTime? = null,
    @CreatedDate
    @Column(name = "created_date", updatable = false, nullable = false)
    var createdDate: LocalDate? = null,
)
