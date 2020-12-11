package munoon.bank.service.transactional.card

import munoon.bank.service.transactional.card.CardTestData.assertMatch
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import java.time.LocalDateTime

internal class CardMapperTest {
    @Test
    fun asTo() {
        val card = Card("CARD_ID", 100, "default", "123456789012", "1111", 100.0, true, LocalDateTime.now())
        val expected = CardTo("CARD_ID", "default","123456789012", 100.0, true, LocalDateTime.now())
        val actual = CardMapper.INSTANCE.asTo(card)
        assertMatch(actual, expected)
    }

    @Test
    fun updateCardByAdmin() {
        val card = Card("CARD_ID", 100, "default", "123456789012", "1111", 100.0, true, LocalDateTime.now())
        val update = AdminUpdateCardTo(101, "gold", "111111111111", true)
        val expected = card.copy(userId = 101, type = "gold", number = "111111111111")
        val actual = CardMapper.INSTANCE.updateCard(update, card)
        assertMatch(actual, expected)
    }

    @Test
    fun createCardByAdmin() {
        val actual = CardMapper.INSTANCE.asCard(
                AdminCreateCardTo(100, "default", "123456789012", "1111", true),
                NoOpPasswordEncoder.getInstance()
        )
        val expected = Card(null, 100, "default", "123456789012", "{noop}1111", 0.0, true, LocalDateTime.now())
        assertMatch(actual, expected)
    }

    @Test
    fun buyCard() {
        val buyCardTo = BuyCardTo("default", "1111", null)
        val expected = Card(null, 100, "default", null, "2222", 0.0, true, LocalDateTime.now())
        assertMatch(CardMapper.INSTANCE.asCard(buyCardTo, 100, "2222"), expected)
    }
}