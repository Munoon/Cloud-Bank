package munook.bank.service.market.product

import com.github.pozo.KotlinBuilder
import lombok.NoArgsConstructor
import munoon.bank.common.validation.money.ValidMoneyCount
import org.hibernate.validator.constraints.Length
import java.time.LocalDateTime
import javax.validation.constraints.Min

@KotlinBuilder
@NoArgsConstructor
data class ProductTo(
    val id: Int,
    val name: String,
    val type: ProductType,
    val count: Int?,
    val price: Double,
    val ableToBuy: Boolean,
    val created: LocalDateTime
)

@NoArgsConstructor
data class SaveProductTo(
    @field:Length(min = 1, max = 30)
    val name: String,

    val type: ProductType,

    @field:Min(0)
    val count: Int?,

    @field:ValidMoneyCount
    val price: Double,

    val ableToBuy: Boolean
)