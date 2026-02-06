package com.example.zero.persistence.repository

import com.example.zero.persistence.entity.OrderItemEntity
import com.example.zero.projections.OrderItemProjection
import com.example.zero.projections.OrderItemUpdateProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface OrderItemRepository : JpaRepository<OrderItemEntity, UUID> {
    @Query("""
        SELECT new com.example.zero.projections.OrderItemProjection(p.id, p.name, i.quantity, i.productPrice)
        FROM OrderItemEntity i
        JOIN i.product p
        WHERE i.order.id = :orderId
    """)
    fun findOrderProducts(@Param("orderId") orderId: UUID): List<OrderItemProjection>

    @Query("""
        SELECT new com.example.zero.projections.OrderItemUpdateProjection(p.id, i.quantity, i.productPrice, i.id)
        FROM OrderItemEntity i
        JOIN i.product p
        WHERE i.order.id = :orderId
    """)
    fun findOrderProductsForUpdate(@Param("orderId") orderId: UUID): List<OrderItemUpdateProjection>

    fun findByOrderIdAndProductId(orderId: UUID, productId: UUID): List<OrderItemEntity>
}