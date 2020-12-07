package munoon.bank.service.transactional.card

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface CardRepository : MongoRepository<Card, String> {
    fun countAllByUserIdAndType(userId: Int, type: String): Int

    fun findAllByUserId(userId: Int): List<Card>

    fun findByNumber(number: String): Optional<Card>
}