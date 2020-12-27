package munoon.bank.service.transactional.transaction

import com.github.pozo.KotlinBuilder
import lombok.NoArgsConstructor
import munoon.bank.common.card.CardTo
import munoon.bank.common.user.UserTo
import munoon.bank.service.transactional.card.CardToWithOwner
import munoon.bank.service.transactional.card.SafeCardToWithOwner
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

@KotlinBuilder
@NoArgsConstructor
data class SafeUserTransactionTo(
        val id: String?,

        val card: SafeCardToWithOwner,

        val price: Double,

        val actualPrice: Double,

        val registered: LocalDateTime,

        val type: UserTransactionType,

        val info: SafeUserTransactionInfoTo?,

        val canceled: Boolean
)

interface UserTransactionInfoTo
interface UnSafeUserTransactionInfoTo : UserTransactionInfoTo
interface SafeUserTransactionInfoTo : UserTransactionInfoTo

@KotlinBuilder
@NoArgsConstructor
data class BuyCardUserTransactionInfoTo(
        var buyCard: CardTo
) : UnSafeUserTransactionInfoTo, SafeUserTransactionInfoTo

@KotlinBuilder
@NoArgsConstructor
data class AwardUserTransactionInfoTo(
        var user: UserTo?,
        var message: String?
) : UnSafeUserTransactionInfoTo, SafeUserTransactionInfoTo

@KotlinBuilder
@NoArgsConstructor
data class FineUserTransactionInfoTo(
        var user: UserTo?,
        var message: String?
) : UnSafeUserTransactionInfoTo, SafeUserTransactionInfoTo

@KotlinBuilder
@NoArgsConstructor
data class TranslateUserTransactionInfoTo(
        var receiveTransaction: UserTransactionTo,
        var message: String?
) : UnSafeUserTransactionInfoTo

@KotlinBuilder
@NoArgsConstructor
data class ReceiveUserTransactionInfoTo(
        var translateTransaction: UserTransactionTo,
        var message: String?
) : UnSafeUserTransactionInfoTo

@KotlinBuilder
@NoArgsConstructor
data class SafeTranslateUserTransactionInfoTo(
        var receiveTransaction: SafeUserTransactionTo,
        var message: String?
) : SafeUserTransactionInfoTo

@KotlinBuilder
@NoArgsConstructor
data class SafeReceiveUserTransactionInfoTo(
        var translateTransaction: SafeUserTransactionTo,
        var message: String?
) : SafeUserTransactionInfoTo