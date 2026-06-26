package com.example.zero.kafka.handler

import com.example.zero.kafka.dto.OrderKafkaEvent

interface OrderKafkaEventHandler {
    fun supports(event: OrderKafkaEvent): Boolean
    fun handle(event: OrderKafkaEvent)
}
