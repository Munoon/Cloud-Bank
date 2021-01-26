package munook.bank.service.market.specimen

import com.github.pozo.KotlinBuilder
import munook.bank.service.market.product.Product
import org.hibernate.validator.constraints.Length
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@KotlinBuilder
@Table(name = "specimens")
data class Specimen (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int?,

    @field:Length(min = 1, max = 200)
    @Column(name = "custom_id", nullable = false)
    var customId: String,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product,

    @Column(name = "able_to_buy", nullable = false)
    var ableToBuy: Boolean,

    @Column(name = "created", nullable = false)
    var created: LocalDateTime
)