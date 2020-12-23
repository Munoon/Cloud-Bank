package munoon.bank.service.transactional.card

import com.github.pozo.KotlinBuilder
import lombok.NoArgsConstructor
import munoon.bank.common.user.UserTo
import munoon.bank.service.transactional.util.validation.CardType
import org.hibernate.validator.constraints.Length
import java.time.LocalDateTime

@KotlinBuilder
@NoArgsConstructor
data class CardToWithOwner(
        val id: String,

        val type: String,

        val number: String?,

        val owner: UserTo?,

        val balance: Double,

        val active: Boolean,

        val registered: LocalDateTime
)

@NoArgsConstructor
data class BuyCardTo(
        @field:CardType
        @field:Length(min = 2, max = 20)
        val type: String,

        @field:Length(min = 4, max = 4)
        val pinCode: String,

        val cardData: CardDataTo?
) {
        override fun toString() = "BuyCardTo(type='$type', cardDataTo=$cardData)"
}

@NoArgsConstructor
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

        @field:CardType
        @field:Length(min = 2, max = 20)
        val type: String,

        @field:Length(min = 12, max = 12)
        val number: String?,

        val active: Boolean
)

@NoArgsConstructor
data class AdminCreateCardTo(
        val userId: Int,

        @field:CardType
        @field:Length(min = 2, max = 20)
        val type: String,

        @field:Length(min = 12, max = 12)
        val number: String?,

        @field:Length(min = 4, max = 4)
        val pinCode: String,

        val active: Boolean
) {
        override fun toString() = "AdminCreateCardTo(userId=$userId, type='$type', number=$number, active=$active)"
}

@NoArgsConstructor
data class UserUpdateCardPinCode(
        @field:Length(min = 4, max = 4)
        val oldPinCode: String,

        @field:Length(min = 4, max = 4)
        val newPinCode: String
) {
        override fun toString() = "UserUpdateCardPinCode()"
}

@NoArgsConstructor
data class AdminUpdateCardPinCode(
        @field:Length(min = 4, max = 4)
        val pinCode: String
) {
        override fun toString() = "AdminUpdateCardPinCode()"
}