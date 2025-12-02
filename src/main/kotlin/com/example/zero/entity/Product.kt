package com.example.zero.entity

import com.example.zero.enums.Category
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "products")
class Product(
    @Id @GeneratedValue
    var id: UUID? = null,
    var name: String,
    @Column(unique = true)
    var productNumber: Long,
    var description: String,
    var category: Category,
    var price: BigDecimal,
    var quantity: Int,
    var timeStampQuantityChanged: LocalDateTime? = null,
    var timeStampCreated: LocalDateTime? = null,
) {

    @PrePersist
    fun onCreate() {
        timeStampQuantityChanged = LocalDateTime.now();
        timeStampCreated = LocalDateTime.now();
    }
}