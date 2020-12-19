package munoon.bank.service.transactional.transaction

import munoon.bank.service.transactional.card.Card
import org.hibernate.validator.constraints.Length
import org.springframework.data.mongodb.core.mapping.DBRef

interface UserTransactionInfo
interface UsersCollectorTransactionInfo {
        fun getUsersId(): Set<Int>
}

data class BuyCardUserTransactionInfo(
        @DBRef
        var buyCard: Card
) : UserTransactionInfo

data class AwardUserTransactionInfo(
        var userId: Int,

        @field:Length(max = 200, message = "Сообщение слишком большое")
        var message: String?
) : UserTransactionInfo, UsersCollectorTransactionInfo {
        override fun getUsersId(): Set<Int> = setOf(userId)
}

data class FineUserTransactionInfo(
        var userId: Int,

        @field:Length(max = 200, message = "Сообщение слишком большое")
        var message: String?
) : UserTransactionInfo, UsersCollectorTransactionInfo {
        override fun getUsersId(): Set<Int> = setOf(userId)
}