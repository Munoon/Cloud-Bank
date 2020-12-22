package munoon.bank.service.transactional.transaction

import munoon.bank.service.transactional.card.Card
import munoon.bank.service.transactional.util.validation.ValidMoneyCount
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class UserTransaction(
        @Id
        val id: String?,

        @DBRef
        val card: Card,

        @field:ValidMoneyCount
        val price: Double,

        val actualPrice: Double,

        val leftBalance: Double,

        val registered: LocalDateTime,

        val type: UserTransactionType,

        val info: UserTransactionInfo?,

        val canceled: Boolean
)

enum class UserTransactionType {
        CARD_BUY, AWARD, FINE
}