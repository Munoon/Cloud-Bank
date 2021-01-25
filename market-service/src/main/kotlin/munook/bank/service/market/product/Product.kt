package munook.bank.service.market.product

import com.github.pozo.KotlinBuilder
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@KotlinBuilder
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int?,

    @Column(name = "name", nullable = false)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: ProductType,

    @Column(name = "count")
    var count: Int?,

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