package com.example.zero.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "rest")
class RestProperties(
    val accountNumber: AccountNumber,
    val inn: Inn
) {
    class AccountNumber(
        val baseUrl: String,
        val getAccountNumbersPath: String
    )

    class Inn(
        val baseUrl: String,
        val getAccountInnsPath: String
    )
}
