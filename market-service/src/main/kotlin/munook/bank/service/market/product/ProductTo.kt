package munook.bank.service.market.product

import lombok.NoArgsConstructor

@NoArgsConstructor
data class SaveProductTo(
    val name: String,
    val type: ProductType,
    val count: Int?,
    val price: Double,
    val ableToBuy: Boolean
)