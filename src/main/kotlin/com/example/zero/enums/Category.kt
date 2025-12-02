package com.example.zero.enums

enum class Category {
    SMARTPHONES,
    LAPTOPS,
    TVS,
    COMPUTERS,
    COMPUTER_PARTS;


    /*
     * Тут осталось с гайда если у нас категория это стринг, хотелось бы спросить
     * лучше в дто что, стринг или енам
     */
    companion object {
        val names by lazy { Category.values().map{ it.name } }
    }
}