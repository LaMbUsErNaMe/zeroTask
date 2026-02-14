package com.example.zero.enums

enum class CategoryType {
    SMARTPHONES,
    LAPTOPS,
    TVS,
    COMPUTERS,
    COMPUTER_PARTS;

    companion object {
        val names by lazy { CategoryType.values().map{ it.name } }
    }
}
