package com.example.zero.extension

import com.example.zero.kafka.dto.CreateOrderKafkaEvent
import com.example.zero.kafka.dto.UpdateOrderKafkaEvent
import com.example.zero.kafka.dto.UpdateOrderStatusKafkaEvent
import com.example.zero.services.dto.order.CreateOrderServiceDto
import com.example.zero.services.dto.order.OrderItemDto
import com.example.zero.services.dto.order.PatchOrderServiceDto
import com.example.zero.services.dto.order.PatchOrderStatusServiceDto

fun CreateOrderKafkaEvent.toCreateOrderServiceDto() = CreateOrderServiceDto(
    customerId = requireNotNull(customerId),
    deliveryAddress = requireNotNull(deliveryAddress),
    products = requireNotNull(products).map {
        OrderItemDto(
            productId = requireNotNull(it.id),
            quantity = requireNotNull(it.quantity)
        )
    }
)

fun UpdateOrderKafkaEvent.toPatchOrderServiceDto() = PatchOrderServiceDto(
    customerId = requireNotNull(customerId),
    deliveryAddress = requireNotNull(deliveryAddress),
    products = requireNotNull(products).map {
        OrderItemDto(
            productId = requireNotNull(it.id),
            quantity = requireNotNull(it.quantity)
        )
    }
)

fun UpdateOrderStatusKafkaEvent.toPatchOrderStatusServiceDto() = PatchOrderStatusServiceDto(
    status = requireNotNull(status)
)
