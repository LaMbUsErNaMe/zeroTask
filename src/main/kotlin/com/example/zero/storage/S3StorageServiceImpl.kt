package com.example.zero.storage

import com.example.zero.properties.S3Properties
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.exception.SdkException
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.HeadBucketRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.IOException
import java.io.InputStream

@Service
class S3StorageServiceImpl(
    private val s3Client: S3Client,
    private val s3Properties: S3Properties
) : S3StorageService {

    override fun checkBucket() {
        execute("Failed to access S3 bucket") {
            val request = HeadBucketRequest.builder()
                .bucket(s3Properties.bucket)
                .build()

            s3Client.headBucket(request)
        }
    }

    override fun upload(
        objectKey: String,
        inputStream: InputStream,
        contentLength: Long,
        contentType: String?
    ) {
        execute("Failed to upload S3 object [$objectKey]") {
            val requestBuilder = PutObjectRequest.builder()
                .bucket(s3Properties.bucket)
                .key(objectKey)

            if (!contentType.isNullOrBlank()) {
                requestBuilder.contentType(contentType)
            }

            s3Client.putObject(
                requestBuilder.build(),
                RequestBody.fromInputStream(
                    inputStream,
                    contentLength
                )
            )
        }
    }

    override fun download(objectKey: String): InputStream =
        execute("Failed to download S3 object [$objectKey]") {
            val request = GetObjectRequest.builder()
                .bucket(s3Properties.bucket)
                .key(objectKey)
                .build()

            s3Client.getObject(request)
        }

    override fun delete(objectKey: String) {
        execute("Failed to delete S3 object [$objectKey]") {
            val request = DeleteObjectRequest.builder()
                .bucket(s3Properties.bucket)
                .key(objectKey)
                .build()

            s3Client.deleteObject(request)
        }
    }

    private fun <T> execute(message: String, action: () -> T): T =
        try {
            action()
        } catch (exception: SdkException) {
            throw S3StorageException(message, exception)
        } catch (exception: IOException) {
            throw S3StorageException(message, exception)
        }
}
