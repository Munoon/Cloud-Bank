package munoon.bank.service.transactional.transaction

import munoon.bank.service.transactional.card.Card
import org.springframework.data.mongodb.core.mapping.DBRef

interface UserTransactionInfo

data class BuyCardUserTransactionInfo(
        @DBRef
        var buyCard: Card
) : UserTransactionInfo

data class AwardUserTransactionInfo(
        var userId: Int,
        var message: String?
) : UserTransactionInfo

data class FineUserTransactionInfo(
        var userId: Int,
        var message: String?
) : UserTransactionInfo