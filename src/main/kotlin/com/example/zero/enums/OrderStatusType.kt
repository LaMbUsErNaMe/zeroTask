package com.example.zero.enums

enum class OrderStatusType {
    CREATED,
    CONFIRMATION_PENDING,
    CONFIRMED,
    CANCELED,
    DONE,
    REJECTED;

    companion object {
        val names by lazy { OrderStatusType.values().map{ it.name } }
    }
}
