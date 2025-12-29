package com.example.zero.search

import com.example.zero.enums.CategoryType
import com.example.zero.enums.OperationType
import java.math.BigDecimal

enum class ProductField(
    val path: String,
    val type: Class<*>,
    val allowedOperations: Set<OperationType>,
    val enumClass: Class<out Enum<*>>? = null
) {
    NAME(
        path = "name",
        type = String::class.java,
        allowedOperations = setOf(OperationType.EQUALS, OperationType.LIKE)
    ),
    PRODUCT_NUMBER(
        path = "productNumber",
        type = Long::class.java,
        allowedOperations = setOf(OperationType.EQUALS, OperationType.GTE, OperationType.LTE)
    ),
    PRICE(
        path = "price",
        type = BigDecimal::class.java,
        allowedOperations = setOf(OperationType.EQUALS, OperationType.GTE, OperationType.LTE)
    ),
    QUANTITY(
        path = "quantity",
        type = BigDecimal::class.java,
        allowedOperations = setOf(OperationType.EQUALS, OperationType.GTE, OperationType.LTE)
    ),
    CATEGORY_TYPE(
        path = "categoryType",
        type = Enum::class.java,
        allowedOperations = setOf(OperationType.EQUALS),
        enumClass = CategoryType::class.java
    );
}
