package com.example.zero

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * Это точка входа в приложение @SpringBootApplication аннотация которая
 * содержит в себе 3 другие аннотации @SpringBootConfiguration - делает этот класс конфигурационным,
 * @EnableAutoConfiguration - включает авто конфиг по типу
 * spring:
 * 	datasource:
 * 		etc....
 * и @ComponentScan - скан бинов
 * профили есть, сокрытие креденталс сделал наверн норм
 *
 * для успешного запуска приложения ставим jdk17, синхронизируем gradle
 * меняем в .yaml юрл, имя, пароль от своей БД, порт сервера томкат в
 * нужном профиле
 */
@EnableJpaAuditing
@SpringBootApplication
class ZeroApplication

fun main(args: Array<String>) {
	runApplication<ZeroApplication>()
}
