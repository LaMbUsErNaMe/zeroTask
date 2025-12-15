package com.example.zero.services

import com.example.zero.persistence.repository.ProductRepository
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ProductServiceTest {

    @Mock
    lateinit var productRepository: ProductRepository

    @Mock

    @InjectMocks
    lateinit var productService: ProductService


}
