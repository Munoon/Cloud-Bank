package munook.bank.service.market.product

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
interface ProductRepository : JpaRepository<Product, Int> {
    fun findAllByAbleToBuyIsTrue(pageable: Pageable): Page<Product>
}