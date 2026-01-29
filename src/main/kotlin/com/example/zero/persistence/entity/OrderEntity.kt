package com.example.zero.persistence.entity;

import com.example.zero.enums.OrderStatusType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.util.UUID

@EntityListeners(AuditingEntityListener::class)
@Entity
@Table(name = "\"order\"")
class OrderEntity (
        @Id @GeneratedValue
        @Column(name = "id", updatable = false, nullable = false)
        var id: UUID? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "customer_id", nullable = false)
        var customer: CustomerEntity,

        @Enumerated(EnumType.STRING)
        @Column(name = "status", updatable = true, nullable = false)
        var status: OrderStatusType = OrderStatusType.CREATED,

        @Column(name = "delivery_address", updatable = true, nullable = false)
        var deliveryAddress: String
)
