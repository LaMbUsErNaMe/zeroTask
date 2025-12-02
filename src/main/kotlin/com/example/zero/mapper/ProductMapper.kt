package com.example.zero.mapper

import com.example.zero.dto.request.ProductRequestDto
import com.example.zero.dto.response.ProductResponseDto
import com.example.zero.entity.Product
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface ProductMapper {
    fun toDto(product: Product): ProductResponseDto;
    fun toEntity(productResponseDto: ProductRequestDto): Product;

}