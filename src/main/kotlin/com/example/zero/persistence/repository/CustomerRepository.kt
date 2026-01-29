package com.example.zero.persistence.repository

import com.example.zero.persistence.entity.CustomerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface CustomerRepository : JpaRepository<CustomerEntity, Long>, JpaSpecificationExecutor<CustomerEntity> {
}