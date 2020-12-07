package munoon.bank.service.transactional.card

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Card(
        @Id
        val id: String?,

        val userId: Int,

        val type: String,

        val number: String?,

        @JsonIgnore
        val pinCode: String,

        val balance: Double
) {
        override fun toString() = "Card(id=$id, userId=$userId, type='$type', number=$number, balance=$balance)"
}