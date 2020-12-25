package munoon.bank.service.transactional.transaction

import munoon.bank.service.transactional.card.Card
import org.hibernate.validator.constraints.Length
import org.springframework.data.mongodb.core.mapping.DBRef

interface UserTransactionInfo
interface UsersCollector {
        fun getUsersId(): Set<Int>
}
interface UserTransactionsIdCollector {
        fun getTransactionsId(): Set<String>
}

data class BuyCardUserTransactionInfo(
        @DBRef
        var buyCard: Card
) : UserTransactionInfo

data class AwardUserTransactionInfo(
        var userId: Int,

        @field:Length(max = 200, message = "Сообщение слишком большое")
        var message: String?
) : UserTransactionInfo, UsersCollector {
        override fun getUsersId(): Set<Int> = setOf(userId)
}

data class FineUserTransactionInfo(
        var userId: Int,

        @field:Length(max = 200, message = "Сообщение слишком большое")
        var message: String?
) : UserTransactionInfo, UsersCollector {
        override fun getUsersId(): Set<Int> = setOf(userId)
}

data class TranslateUserTransactionInfo(
        var receiveTransactionId: String,
        var receiverUserId: Int,

        @field:Length(max = 200, message = "Сообщение слишком большое")
        var message: String?
) : UserTransactionInfo, UsersCollector, UserTransactionsIdCollector {
        override fun getUsersId(): Set<Int> = setOf(receiverUserId)
        override fun getTransactionsId(): Set<String> = setOf(receiveTransactionId)
}

data class ReceiveUserTransactionInfo(
        var translateTransactionId: String,
        var translateUserId: Int,

        @field:Length(max = 200, message = "Сообщение слишком большое")
        var message: String?
) : UserTransactionInfo, UsersCollector, UserTransactionsIdCollector {
        override fun getUsersId(): Set<Int> = setOf(translateUserId)
        override fun getTransactionsId(): Set<String> = setOf(translateTransactionId)
}