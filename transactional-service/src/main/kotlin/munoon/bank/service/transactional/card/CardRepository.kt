package munoon.bank.service.transactional.card

import com.mongodb.client.result.UpdateResult
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
    fun deactivateAllByUserId(userId: Int): UpdateResult
    fun deactivateCard(cardId: String): UpdateResult
}

@Repository
class UpdateCardRepositoryImpl(private val mongoTemplate: MongoTemplate) : UpdateCardRepository {
    override fun deactivateAllByUserId(userId: Int): UpdateResult {
        val query = Query(Criteria.where("userId").`is`(userId))
        val update = Update().apply { set("active", false) }
        return mongoTemplate.updateMulti(query, update, Card::class.java)
    }

    override fun deactivateCard(cardId: String): UpdateResult {
        val query = Query(Criteria.where("id").`is`(cardId))
        val update = Update().apply { set("active", false) }
        return mongoTemplate.updateMulti(query, update, Card::class.java)
    }
}