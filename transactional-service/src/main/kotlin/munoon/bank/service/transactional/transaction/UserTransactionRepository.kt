package munoon.bank.service.transactional.transaction

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface UserTransactionRepository : MongoRepository<UserTransaction, String> {
    fun getAllByCardId(cardId: String, pageable: Pageable): Page<UserTransaction>
}