package munoon.bank.service.transactional.transaction

import com.github.pozo.KotlinBuilder
import lombok.NoArgsConstructor
import munoon.bank.service.transactional.card.CardTo
import java.time.LocalDateTime

@KotlinBuilder
@NoArgsConstructor
data class UserTransactionTo(
        val id: String?,

        val card: CardTo,

        val price: Double,

        val leftBalance: Double,

        val registered: LocalDateTime,

        val type: UserTransactionType,

        val info: UserTransactionInfoTo?
)

interface UserTransactionInfoTo

@KotlinBuilder
@NoArgsConstructor
data class BuyCardUserTransactionInfoTo(
        var buyCard: CardTo,
        var actualPrice: Double
) : UserTransactionInfoTo

@KotlinBuilder
@NoArgsConstructor
data class AwardUserTransactionInfoTo(
        var userId: Int,
        var message: String?,
        var actualPrice: Double
) : UserTransactionInfoTo

@KotlinBuilder
@NoArgsConstructor
data class FineUserTransactionInfoTo(
        var userId: Int,
        var message: String?,
        var actualPrice: Double
) : UserTransactionInfoTo