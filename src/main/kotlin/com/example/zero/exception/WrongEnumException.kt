package com.example.zero.exception

class WrongEnumException(message: String) : RuntimeException(message) {
    /**
     * знаю что нужно наверное, но не знаю как кидать эксепшн быстрее
     * спринга если у меня поле в dto Enum
     */
}