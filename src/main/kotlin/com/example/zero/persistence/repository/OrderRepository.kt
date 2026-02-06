package com.example.zero.persistence.repository

import com.example.zero.enums.OrderStatusType
import com.example.zero.persistence.entity.OrderEntity
import com.example.zero.projections.OrderInfoProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface OrderRepository : JpaRepository<OrderEntity, UUID>, JpaSpecificationExecutor<OrderEntity> {
    @Modifying
    @Query("UPDATE OrderEntity o SET o.status = :status WHERE o.id = :id")
    fun updateStatus(@Param("id") id: UUID, @Param("status") status: OrderStatusType): Int

    //fun findByStatusIn(statuses: List<OrderStatusType>): List<OrderEntity>

    @Query("""
    select new com.example.zero.projections.OrderInfoProjection(
        o.id,
        c.id,
        c.login,
        o.status,
        o.deliveryAddress,
        oi.quantity
    )
    from OrderEntity o
    join o.customer c
    join OrderItemEntity oi on oi.order = o
    where o.status in :statuses
      and oi.product.id = :productId
""")
    fun findOrdersInfoRowsByProduct(
        @Param("productId") productId: UUID,
        @Param("statuses") statuses: List<OrderStatusType>
    ): List<OrderInfoProjection>

}