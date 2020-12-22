package munoon.bank.service.transactional.transaction

import com.github.pozo.KotlinBuilder
import lombok.NoArgsConstructor
import munoon.bank.common.user.UserTo
import munoon.bank.service.transactional.card.CardTo
import munoon.bank.service.transactional.card.CardToWithOwner
import java.time.LocalDateTime

@KotlinBuilder
@NoArgsConstructor
data class UserTransactionTo(
        val id: String?,

        val card: CardToWithOwner,

        val price: Double,

        val actualPrice: Double,

        val leftBalance: Double,

        val registered: LocalDateTime,

        val type: UserTransactionType,

        val info: UserTransactionInfoTo?,

        val canceled: Boolean
)

interface UserTransactionInfoTo

@KotlinBuilder
@NoArgsConstructor
data class BuyCardUserTransactionInfoTo(
        var buyCard: CardTo
) : UserTransactionInfoTo

@KotlinBuilder
@NoArgsConstructor
data class AwardUserTransactionInfoTo(
        var user: UserTo?,
        var message: String?
) : UserTransactionInfoTo

@KotlinBuilder
@NoArgsConstructor
data class FineUserTransactionInfoTo(
        var user: UserTo?,
        var message: String?
) : UserTransactionInfoTo