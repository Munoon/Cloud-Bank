package munoon.bank.service.transactional.card

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

interface CardRepository : MongoRepository<Card, String>, UpdateCardRepository {
    fun countAllByUserIdAndType(userId: Int, type: String): Int

    fun findAllByUserId(userId: Int): List<Card>

    fun findByNumber(number: String): Optional<Card>
}

interface UpdateCardRepository {
    fun deactivateAllByUserId(userId: Int)
}

@Repository
class UpdateCardRepositoryImpl(private val mongoTemplate: MongoTemplate) : UpdateCardRepository {
    override fun deactivateAllByUserId(userId: Int) {
        val query = Query(Criteria.where("userId").`is`(userId))
        val update = Update().apply { set("active", false) }
        mongoTemplate.updateMulti(query, update, Card::class.java)
    }
}