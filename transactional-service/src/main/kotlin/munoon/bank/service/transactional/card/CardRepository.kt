package munoon.bank.service.transactional.card

import com.mongodb.client.result.UpdateResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import java.util.*

interface CardRepository : MongoRepository<Card, String>, UpdateCardRepository {
    fun countAllByUserIdAndType(userId: Int, type: String): Int

    fun countAllByUserId(userId: Int): Int

    fun findAllByUserId(userId: Int): List<Card>

    fun findByNumber(number: String): Optional<Card>

    fun findByUserIdAndPrimaryTrue(userId: Int): Optional<Card>
}

interface UpdateCardRepository {
    fun deactivateAllByUserId(userId: Int): UpdateResult
    fun deactivateCard(cardId: String): UpdateResult
    fun makeAllUnPrimaryByUserId(userId: Int): UpdateResult
    fun findWithService(pageable: Pageable): Page<Card>
}

@Repository
class UpdateCardRepositoryImpl(private val mongoTemplate: MongoTemplate,
                               private val cardsProperties: CardsProperties) : UpdateCardRepository {
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

    override fun makeAllUnPrimaryByUserId(userId: Int): UpdateResult {
        val query = Query(Criteria.where("userId").`is`(userId))
        val update = Update().apply { set("primary", false) }
        return mongoTemplate.updateMulti(query, update, Card::class.java)
    }

    override fun findWithService(pageable: Pageable): Page<Card> {
        val cards = cardsProperties.cards.entries.filter { it.value.service != 0.0 }.map { it.key }
        val query = Query(Criteria.where("type").`in`(cards)).with(pageable)
        val content = mongoTemplate.find(query, Card::class.java)
        return PageableExecutionUtils.getPage(content, pageable)
            { mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Card::class.java) }
    }
}