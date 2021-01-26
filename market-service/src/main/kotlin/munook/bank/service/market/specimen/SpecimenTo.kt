package munook.bank.service.market.specimen

import com.github.pozo.KotlinBuilder
import lombok.NoArgsConstructor
import munook.bank.service.market.product.ProductTo
import org.hibernate.validator.constraints.Length
import java.time.LocalDateTime

@KotlinBuilder
@NoArgsConstructor
data class SpecimenTo(
    val id: Int,
    val customId: String,
    val product: ProductTo,
    val ableToBuy: Boolean,
    val created: LocalDateTime
)

@NoArgsConstructor
data class SaveSpecimenTo(
    @field:Length(min = 1, max = 200)
    val customId: String,
    val productId: Int,
    val ableToBuy: Boolean
)