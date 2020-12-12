package munoon.bank.service.transactional.transaction

import lombok.NoArgsConstructor

@NoArgsConstructor
data class FineAwardDataTo(
        val card: String,
        val count: Double,
        val type: FineAwardType,
        val message: String?
)

enum class FineAwardType {
    FINE, AWARD
}