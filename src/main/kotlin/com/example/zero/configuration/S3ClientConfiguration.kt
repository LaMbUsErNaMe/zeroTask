package com.example.zero.configuration

import com.example.zero.properties.S3Properties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class S3ClientConfiguration {

    @Bean(destroyMethod = "close")
    fun s3Client(properties: S3Properties): S3Client =
        S3Client.builder()
            .endpointOverride(properties.endpoint)
            .region(Region.of(properties.region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        properties.accessKey,
                        properties.secretKey
                    )
                )
            )
            .forcePathStyle(true)
            .build()
}
