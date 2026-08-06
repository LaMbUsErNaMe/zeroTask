package com.example.zero.kafka.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import jakarta.validation.constraints.NotBlank

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "event",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(value = CreateOrderKafkaEvent::class, name = "CREATE_ORDER"),
    JsonSubTypes.Type(value = UpdateOrderKafkaEvent::class, name = "UPDATE_ORDER"),
    JsonSubTypes.Type(value = DeleteOrderKafkaEvent::class, name = "DELETE_ORDER"),
    JsonSubTypes.Type(value = UpdateOrderStatusKafkaEvent::class, name = "UPDATE_ORDER_STATUS")
)
sealed interface OrderKafkaEvent {
    @get:NotBlank
    val event: String
}
