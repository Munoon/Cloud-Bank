package munoon.bank.service.transactional.transaction

import com.github.pozo.KotlinBuilder
import lombok.NoArgsConstructor
import munoon.bank.service.transactional.card.CardTo

@KotlinBuilder
@NoArgsConstructor
data class UserTransactionTo(
        val id: String?,

        val card: CardTo,

        val price: Double,

        val leftBalance: Double,

        val type: UserTransactionType,

        val info: UserTransactionInfoTo?
)

interface UserTransactionInfoTo

@KotlinBuilder
@NoArgsConstructor
data class BuyCardUserTransactionInfoTo(
        var buyCard: CardTo
) : UserTransactionInfoTo