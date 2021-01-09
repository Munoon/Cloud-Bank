package munoon.bank.service.transactional.transaction

import munoon.bank.common.validation.money.ValidMoneyCount
import munoon.bank.service.transactional.card.Card
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
) : UsersCollector {
        override fun getUsersId(): Set<Int> {
                val users = mutableSetOf(card.userId)
                if (info is UsersCollector) {
                        users.addAll(info.getUsersId())
                }
                return users
        }

}

enum class UserTransactionType {
        CARD_BUY, AWARD, FINE, TRANSLATE_MONEY, RECEIVE_MONEY, SALARY, CARD_SERVICE
}