package munoon.bank.service.transactional.card;

import munoon.bank.service.transactional.card.CardTestData.assertMatch
import org.junit.jupiter.api.Test;

internal class CardMapperTest {
    @Test
    fun asSelfTo() {
        val card = Card("CARD_ID", 100, "default", "123456789012", "1111", 100.0)
        val expected = CardTo("CARD_ID", "default","123456789012", 100.0)
        val actual = CardMapper.INSTANCE.asTo(card)
        assertMatch(actual, expected)
    }
}