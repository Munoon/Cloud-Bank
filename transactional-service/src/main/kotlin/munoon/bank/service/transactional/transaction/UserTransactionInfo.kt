package munoon.bank.service.transactional.transaction

import munoon.bank.service.transactional.card.Card
import munoon.bank.service.transactional.util.validation.ValidMoneyCount
import org.hibernate.validator.constraints.Length
import org.springframework.data.mongodb.core.mapping.DBRef

interface UserTransactionInfo

data class BuyCardUserTransactionInfo(
        @DBRef
        var buyCard: Card,

        @field:ValidMoneyCount
        var actualPrice: Double
) : UserTransactionInfo

data class AwardUserTransactionInfo(
        var userId: Int,

        @field:Length(max = 200, message = "Сообщение слишком большое")
        var message: String?,

        @field:ValidMoneyCount
        var actualPrice: Double
) : UserTransactionInfo

data class FineUserTransactionInfo(
        var userId: Int,

        @field:Length(max = 200, message = "Сообщение слишком большое")
        var message: String?,

        @field:ValidMoneyCount
        var actualPrice: Double
) : UserTransactionInfo