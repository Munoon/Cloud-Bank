package munoon.bank.service.transactional.transaction

import lombok.NoArgsConstructor
import munoon.bank.service.transactional.util.validation.ValidMoneyCount
import org.hibernate.validator.constraints.Length

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

enum class FineAwardType {
    FINE, AWARD
}