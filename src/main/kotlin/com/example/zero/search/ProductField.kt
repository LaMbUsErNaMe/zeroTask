package com.example.zero.search

import com.example.zero.enums.CategoryType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

enum class ProductField(
    val path: String,
    val type: Class<*>,
    val enumClass: Class<out Enum<*>>? = null
) {
    NAME(
        path = "name",
        type = String::class.java,
    ),
    PRODUCT_NUMBER(
        path = "productNumber",
        type = Long::class.java,
    ),
    PRICE(
        path = "price",
        type = BigDecimal::class.java,
    ),
    QUANTITY(
        path = "quantity",
        type = BigDecimal::class.java,
    ),
    CREATED_AT(
        path = "createdDate",
        type = LocalDate::class.java,
    ),
    QUANTITY_CHANGED(
        path = "quantityChangedDateTime",
        type = LocalDateTime::class.java,
    ),
    CATEGORY_TYPE(
        path = "categoryType",
        type = Enum::class.java,
        enumClass = CategoryType::class.java
    );

    companion object {
        fun from(field: String): ProductField =
            entries.firstOrNull { it.path == field }
                ?: throw IllegalArgumentException("Такого поля для поиска нет: $field")
    }
}
