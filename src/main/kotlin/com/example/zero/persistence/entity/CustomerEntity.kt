package com.example.zero.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@EntityListeners(AuditingEntityListener::class)
@Entity
@Table(name = "customer")
class CustomerEntity (
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq_gen")
    @SequenceGenerator(name = "customer_seq_gen", sequenceName = "customer_seq", allocationSize = 1)
    @Column(name = "id", updatable = true, nullable = false)
    var id: Long? = null,

    @Column(name = "login", updatable = true, nullable = false, unique = true)
    var login: String,

    @Column(name = "email", updatable = true, nullable = false, unique = true)
    var email: String,

    @Column(name = "is_active", updatable = true, nullable = false)
    var isActive: Boolean
)
