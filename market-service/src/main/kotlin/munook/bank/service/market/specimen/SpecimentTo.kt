package munook.bank.service.market.specimen

import lombok.NoArgsConstructor

@NoArgsConstructor
data class SaveSpecimenTo(
    val customId: String,
    val productId: Int,
    val ableToBuy: Boolean
)