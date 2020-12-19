package munoon.bank.service.transactional.card

import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.card.CardTestData.assertMatch
import munoon.bank.service.transactional.user.UserTestData
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import java.time.LocalDateTime

internal class CardMapperTest : AbstractTest() {
    @Autowired
    private lateinit var cardMapper: CardMapper

    @Test
    fun asTo() {
        val card = Card("CARD_ID", 100, "default", "123456789012", "1111", 100.0, true, LocalDateTime.now())
        val expected = CardTo("CARD_ID", "default","123456789012", 100.0, true, LocalDateTime.now())
        val actual = cardMapper.asTo(card)
        assertMatch(actual, expected)
    }

    @Test
    fun asToWithCustomUser() {
        val card = Card("CARD_ID", 100, "default", "123456789012", "1111", 100.0, true, LocalDateTime.now())
        val expected = CardToWithOwner("CARD_ID", "default", "123456789012", UserTestData.DEFAULT_USER_TO, 100.0, true, card.registered)
        val actual = cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO)
        assertMatch(actual, expected)
    }

    @Test
    fun asToWithUser() {
        val card = Card("CARD_ID", 100, "default", "123456789012", "1111", 100.0, true, LocalDateTime.now())
        val expected = CardToWithOwner("CARD_ID", "default", "123456789012", UserTestData.DEFAULT_USER_TO, 100.0, true, card.registered)
        val actual = cardMapper.asToWithUser(card)
        assertMatch(actual, expected)
    }

    @Test
    fun asToWithNullUser() {
        val card = Card("CARD_ID", 999, "default", "123456789012", "1111", 100.0, true, LocalDateTime.now())
        val expected = CardToWithOwner("CARD_ID", "default", "123456789012", null, 100.0, true, card.registered)
        val actual = cardMapper.asToWithUser(card)
        assertMatch(actual, expected)
    }

    @Test
    fun updateCardByAdmin() {
        val card = Card("CARD_ID", 100, "default", "123456789012", "1111", 100.0, true, LocalDateTime.now())
        val update = AdminUpdateCardTo(101, "gold", "111111111111", true)
        val expected = card.copy(userId = 101, type = "gold", number = "111111111111")
        val actual = cardMapper.updateCard(update, card)
        assertMatch(actual, expected)
    }

    @Test
    fun createCardByAdmin() {
        val actual = cardMapper.asCard(
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
        assertMatch(cardMapper.asCard(buyCardTo, 100, "2222"), expected)
    }
}