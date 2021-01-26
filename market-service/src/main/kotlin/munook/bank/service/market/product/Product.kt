package munook.bank.service.market.product

import com.github.pozo.KotlinBuilder
import munoon.bank.common.validation.money.ValidMoneyCount
import org.hibernate.validator.constraints.Length
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.Min

@Entity
@KotlinBuilder
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int?,

    @field:Length(min = 1, max = 30)
    @Column(name = "name", nullable = false)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: ProductType,

    @field:Min(0)
    @Column(name = "count")
    var count: Int?,

    @field:ValidMoneyCount
    @Column(name = "price", nullable = false)
    var price: Double,

    @Column(name = "able_to_buy", nullable = false)
    var ableToBuy: Boolean,

    @Column(name = "created", nullable = false)
    var created: LocalDateTime
)

enum class ProductType {
    FIXED_SPECIMEN, UNFIXED_SPECIMEN
}