package munoon.bank.service.transactional.transaction

import munoon.bank.service.transactional.card.Card
import org.springframework.data.mongodb.core.mapping.DBRef

interface UserTransactionInfo

data class BuyCardUserTransactionInfo(
        @DBRef
        var buyCard: Card
) : UserTransactionInfo