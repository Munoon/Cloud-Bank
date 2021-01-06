package munoon.bank.service.transactional.card

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.pozo.KotlinBuilder
import org.hibernate.validator.constraints.Length
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
@KotlinBuilder
data class Card(
        @Id
        var id: String?,

        var userId: Int,

        @field:Length(min = 2, max = 20)
        var type: String,

        @Indexed(unique = true)
        @field:Length(min = 12, max = 12)
        var number: String?,

        @JsonIgnore
        var pinCode: String,

        var balance: Double,

        var active: Boolean,

        var primary: Boolean,

        var registered: LocalDateTime
) {
        override fun toString() =
                "Card(id=$id, userId=$userId, type='$type', number=$number, balance=$balance, active=$active, primary=$primary, registered=$registered)"
}