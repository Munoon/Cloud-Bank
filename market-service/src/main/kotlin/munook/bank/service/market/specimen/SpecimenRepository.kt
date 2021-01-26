package munook.bank.service.market.specimen

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
interface SpecimenRepository : JpaRepository<Specimen, Int> {
    fun findAllByProductIdAndAbleToBuyIsTrue(productId: Int, pageable: Pageable): Page<Specimen>
    fun findAllByProductId(productId: Int, pageable: Pageable): Page<Specimen>
    fun findByCustomId(customId: String): Specimen?
}