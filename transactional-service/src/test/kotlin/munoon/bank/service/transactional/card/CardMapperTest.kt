package munoon.bank.service.transactional.card

import munoon.bank.common.card.CardTo
import munoon.bank.common.user.UserTo
import munoon.bank.service.transactional.AbstractTest
import munoon.bank.service.transactional.card.CardTestData.assertMatch
import munoon.bank.service.transactional.user.UserTestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime

internal class CardMapperTest : AbstractTest() {
    @Autowired
    private lateinit var cardMapper: CardMapper

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun asTo() {
        val card = Card("CARD_ID", 100, "default", "123456789012", "1111", 100.0, active = true, primary = true, registered = LocalDateTime.now())
        val expected = CardTo("CARD_ID", 100, "default", "123456789012", 100.0, true, true, LocalDateTime.now())
        val actual = cardMapper.asTo(card)
        assertMatch(actual, expected)
    }

    @Test
    fun asToWithCustomUser() {
        val card = Card("CARD_ID", 100, "default", "123456789012", "1111", 100.0, active = true, primary = true, registered = LocalDateTime.now())
        val expected = CardToWithOwner("CARD_ID", "default", "123456789012", UserTestData.DEFAULT_USER_TO, 100.0, active = true, primary = true, registered = card.registered)
        val actual = cardMapper.asTo(card, UserTestData.DEFAULT_USER_TO)
        assertMatch(actual, expected)
    }

    @Test
    fun asToWithUser() {
        val card = Card("CARD_ID", 100, "default", "123456789012", "1111", 100.0, active = true, primary = true, registered = LocalDateTime.now())
        val expected = CardToWithOwner("CARD_ID", "default", "123456789012", UserTestData.DEFAULT_USER_TO, 100.0, active = true, primary = true, registered = card.registered)
        val actual = cardMapper.asToWithUser(card)
        assertMatch(actual, expected)
    }

    @Test
    fun asToWithNullUser() {
        val card = Card("CARD_ID", 999, "default", "123456789012", "1111", 100.0, active = true, primary = true, LocalDateTime.now())
        val expected = CardToWithOwner("CARD_ID", "default", "123456789012", null, 100.0, active = true, primary = true, card.registered)
        val actual = cardMapper.asToWithUser(card)
        assertMatch(actual, expected)
    }

    @Test
    fun asSafeTo() {
        val card = Card("CARD_ID", 100, "default", "123456789012", "1111", 100.0, active = true, primary = true, LocalDateTime.now())
        val expected = SafeCardToWithOwner("CARD_ID", "123456789012", UserTestData.DEFAULT_USER_TO)
        val actual = cardMapper.asSafeTo(card)
        assertMatch(actual, expected)
    }

    @Test
    fun asSafeToWithCustomUser() {
        val user = UserTo(999, "test", "test", "test", "10", LocalDateTime.now(), emptySet())
        val card = Card("CARD_ID", 100, "default", "123456789012", "1111", 100.0, active = true, primary = true, LocalDateTime.now())
        val expected = SafeCardToWithOwner("CARD_ID", "123456789012", user)
        val actual = cardMapper.asSafeTo(card, user)
        assertMatch(actual, expected)
    }

    @Test
    fun updateCardByAdmin() {
        val card = Card("CARD_ID", 100, "default", "123456789012", "1111", 100.0, active = true, primary = true, LocalDateTime.now())
        val update = AdminUpdateCardTo(101, "gold", "111111111111", true)
        val expected = card.copy(userId = 101, type = "gold", number = "111111111111")
        val actual = cardMapper.updateCard(update, card)
        assertMatch(actual, expected)
    }

    @Test
    fun createCardByAdmin() {
        val actual = cardMapper.asCard(AdminCreateCardTo(100, "default", "123456789012", "1111", true), true)
        val expected = Card(null, 100, "default", "123456789012", "{noop}1111", 0.0, active = true, primary = true, LocalDateTime.now())
        assertMatch(actual, expected)
        assertThat(passwordEncoder.matches("1111", actual.pinCode)).isTrue()
    }

    @Test
    fun buyCard() {
        val buyCardTo = BuyCardTo("default", "1111", null)
        val expected = Card(null, 100, "default", null, "2222", 0.0, active = true, primary = true, LocalDateTime.now())
        val actual = cardMapper.asCard(buyCardTo, 100, true)
        assertMatch(actual, expected)
        assertThat(passwordEncoder.matches("1111", actual.pinCode)).isTrue()
    }
}