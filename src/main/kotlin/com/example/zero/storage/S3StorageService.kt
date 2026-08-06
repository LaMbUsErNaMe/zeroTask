package com.example.zero.storage

import java.io.InputStream

interface S3StorageService {

    fun checkBucket()

    fun upload(objectKey: String, inputStream: InputStream, contentLength: Long, contentType: String?)

    fun download(objectKey: String): InputStream

    fun delete(objectKey: String)
}
