package com.example.zero.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(prefix = "storage.s3")
data class S3Properties(
    val endpoint: URI,
    val region: String,
    val accessKey: String,
    val secretKey: String,
    val bucket: String
)
