package munoon.bank.service.transactional.config

import munoon.bank.service.transactional.card.Card
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index

@Configuration
class MongoConfig {
    companion object {
        val UNIQUE_CARD_NUMBER_INDEX = "UNIQUE_CARD_NUMBER_INDEX"
    }

    @Bean
    fun createIndexes(mongoTemplate: MongoTemplate) = CommandLineRunner {
        mongoTemplate.indexOps(Card::class.java)
                .ensureIndex(Index("number", Sort.Direction.ASC).unique().sparse().named(UNIQUE_CARD_NUMBER_INDEX))
    }
}