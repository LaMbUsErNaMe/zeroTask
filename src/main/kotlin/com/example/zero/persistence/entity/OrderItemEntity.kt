package com.example.zero.persistence.entity

import com.example.zero.enums.OrderStatusType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.util.UUID

@EntityListeners(AuditingEntityListener::class)
@Entity
@Table(name = "order_item")
class OrderItemEntity(
    @Id @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    var order: OrderEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: ProductEntity,

    @Column(name = "product_price", updatable = true, nullable = false)
    var productPrice: BigDecimal,

    @Column(name = "quantity", updatable = true, nullable = false)
    var quantity: BigDecimal,
)
