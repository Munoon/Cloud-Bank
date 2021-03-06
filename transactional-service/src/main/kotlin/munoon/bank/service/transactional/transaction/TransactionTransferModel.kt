package munoon.bank.service.transactional.transaction

import lombok.NoArgsConstructor
import munoon.bank.common.validation.money.ValidMoneyCount
import munoon.bank.service.transactional.card.CardDataTo
import org.hibernate.validator.constraints.Length
import javax.validation.Valid

@NoArgsConstructor
data class FineAwardDataTo(
        @field:Length(min = 12, max = 12)
        val card: String,

        @field:ValidMoneyCount
        val count: Double,

        val type: FineAwardType,

        @field:Length(max = 200, message = "Сообщение слишком большое")
        val message: String?
)

enum class FineAwardType(val originalType: UserTransactionType) {
    FINE(UserTransactionType.FINE), AWARD(UserTransactionType.AWARD)
}

@NoArgsConstructor
data class TranslateMoneyDataTo(
        @field:Length(min = 12, max = 12)
        val receiver: String,

        @field:ValidMoneyCount
        val count: Double,

        @field:Length(max = 200, message = "Сообщение слишком большое")
        val message: String?,

        @field:Valid
        val cardData: CardDataTo
)