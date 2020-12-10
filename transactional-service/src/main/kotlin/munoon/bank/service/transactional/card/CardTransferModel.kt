package munoon.bank.service.transactional.card

import com.github.pozo.KotlinBuilder
import lombok.NoArgsConstructor
import munoon.bank.service.transactional.util.validation.CardType
import org.hibernate.validator.constraints.Length
import java.time.LocalDateTime

@KotlinBuilder
@NoArgsConstructor
data class CardTo(
        val id: String,

        val type: String,

        val number: String?,

        val balance: Double,

        val registered: LocalDateTime
)

@NoArgsConstructor
data class BuyCardTo(
        @field:CardType
        val type: String,

        @field:Length(min = 4, max = 4)
        val pinCode: String,

        val cardData: CardDataTo?
) {
        override fun toString() = "BuyCardTo(type='$type', cardDataTo=$cardData)"
}

data class CardDataTo(
        @field:Length(min = 12, max = 12)
        val card: String,

        @field:Length(min = 4, max = 4)
        val pinCode: String
) {
        override fun toString() = "CardDataTo(card=$card)"
}

@NoArgsConstructor
data class AdminUpdateCardTo(
        val userId: Int,

        @field:Length(min = 2)
        val type: String,

        @field:Length(min = 12, max = 12)
        val number: String?
)

@NoArgsConstructor
data class AdminCreateCardTo(
        val userId: Int,

        @field:Length(min = 2)
        val type: String,

        @field:Length(min = 12, max = 12)
        val number: String?,

        @field:Length(min = 4, max = 4)
        val pinCode: String
)