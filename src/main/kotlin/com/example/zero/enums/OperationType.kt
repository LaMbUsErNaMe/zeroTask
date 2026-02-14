package com.example.zero.enums

import com.fasterxml.jackson.annotation.JsonCreator

enum class OperationType(val string: String) {
    EQUALS("="),
    GTE(">="),
    LTE("<="),
    LIKE("~");
    companion object {
        @JvmStatic
        @JsonCreator
        fun from(value: String): OperationType =
            entries.firstOrNull {
                it.name.equals(value, true) || it.string == value
            } ?: throw IllegalArgumentException("Unknown operation: $value")
    }
}
