package munoon.bank.service.transactional.card

import munoon.bank.common.util.exception.NotFoundException
import munoon.bank.service.transactional.transaction.UserTransactionService
import munoon.bank.service.transactional.util.NotEnoughBalanceException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CardService(private val cardRepository: CardRepository,
                  private val cardsProperties: CardsProperties,
                  private val passwordEncoder: PasswordEncoder,
                  private val userTransactionService: UserTransactionService) {
    fun buyCard(userId: Int, buyCardTo: BuyCardTo): Card {
        val cardType = cardsProperties.cards.find { it.codeName == buyCardTo.type }
                ?: throw NotFoundException("Card with code name ${buyCardTo.type} is not found!")

        if (!cardType.ableToBuy) {
            throw AccessDeniedException("You can't buy ${cardType.name} card!")
        }

        if (cardType.clientLimit != null) {
            val alreadyHaveCards = cardRepository.countAllByUserIdAndType(userId, buyCardTo.type)
            if (alreadyHaveCards >= cardType.clientLimit!!) {
                throw AccessDeniedException("You have $alreadyHaveCards cards of type ${cardType.name}. You can buy maximum ${cardType.clientLimit} card(s)!")
            }
        }

        val userTransaction = when {
            cardType.price == 0.0 && buyCardTo.cardData == null -> null
            else -> userTransactionService.buyCardTransaction(userId, cardType.price, buyCardTo.cardData
                    ?: throw AccessDeniedException("You should specify card for doing this operation."))
        }

        val pinCode = passwordEncoder.encode(buyCardTo.pinCode)
        val buyCard = cardRepository.save(Card(null, userId, cardType.codeName, null, pinCode, 0.0, LocalDateTime.now()))
        if (userTransaction != null) {
            userTransactionService.addCardToCardTransaction(userTransaction, buyCard)
        }

        return buyCard
    }

    fun getCards(userId: Int): List<Card> {
        return cardRepository.findAllByUserId(userId)
    }

    fun getCardByNumberAndValidatePinCode(cardNumber: String, pinCode: String): Card {
        val card = getCardByNumber(cardNumber)
        if (!passwordEncoder.matches(pinCode, card.pinCode)) {
            throw AccessDeniedException("Incorrect pin code!")
        }
        return card;
    }

    fun getCardByNumber(cardNumber: String): Card = cardRepository.findByNumber(cardNumber)
                .orElseThrow { NotFoundException("Card with number '$cardNumber' is not found!") }

    fun minusMoney(card: Card, price: Double): Card {
        if (card.balance < price) {
            throw NotEnoughBalanceException("На карте недостаточно средств!")
        }

        return cardRepository.save(card.copy(balance = card.balance - price))
    }

    fun getCardById(cardId: String): Card = cardRepository.findById(cardId)
            .orElseThrow { NotFoundException("Card with id '$cardId' is not found!") }
}