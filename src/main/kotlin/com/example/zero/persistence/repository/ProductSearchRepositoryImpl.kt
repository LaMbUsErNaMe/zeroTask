package com.example.zero.persistence.repository

import com.example.zero.controller.dto.request.search.SearchFilterDto
import com.example.zero.persistence.entity.ProductEntity
import com.example.zero.search.ProductCriteriaPredicateBuilder
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class ProductSearchRepositoryImpl(
    private val em: EntityManager,
    private val predicateBuilder: ProductCriteriaPredicateBuilder
): ProductSearchRepository {
    override fun search(
        request: List<SearchFilterDto>,
        pageable: Pageable
    ): Page<ProductEntity> {
        val cb = em.criteriaBuilder

        val cq = cb.createQuery(ProductEntity::class.java)
        val root = cq.from(ProductEntity::class.java)

        val predicates = predicateBuilder.build(request, cb, root)
        cq.where(*predicates.toTypedArray())

        val query = em.createQuery(cq)
        query.firstResult = pageable.offset.toInt()
        query.maxResults = pageable.pageSize

        val content = query.resultList

        val countCq = cb.createQuery(Long::class.java)
        val countRoot = countCq.from(ProductEntity::class.java)
        countCq.select(cb.count(countRoot))
            .where(
                *predicateBuilder
                    .build(request, cb, countRoot)
                    .toTypedArray()
            )

        val total = em.createQuery(countCq).singleResult

        return PageImpl(content, pageable, total)
    }

}
