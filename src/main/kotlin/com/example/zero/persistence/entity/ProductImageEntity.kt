package com.example.zero.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@EntityListeners(AuditingEntityListener::class)
@Entity
@Table(name = "product_image")
class ProductImageEntity(
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, updatable = false)
    var product: ProductEntity,

    @Column(name = "object_key", nullable = false, unique = true, length = 512)
    var objectKey: String,

    @Column(name = "original_name", nullable = false, length = 255)
    var originalName: String,

    @Column(name = "content_type", length = 255)
    var contentType: String? = null,

    @Column(name = "size", nullable = false)
    var size: Long,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null
)
