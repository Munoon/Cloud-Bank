package munoon.bank.service.transactional.card

import munoon.bank.common.util.exception.FieldValidationException
import munoon.bank.common.util.exception.NotFoundException
import munoon.bank.service.transactional.config.MongoConfig
import munoon.bank.service.transactional.transaction.UserTransactionService
import munoon.bank.service.transactional.util.CardUtils
import munoon.bank.service.transactional.util.NotEnoughBalanceException
import org.springframework.dao.DuplicateKeyException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class CardService(private val cardRepository: CardRepository,
                  private val cardsProperties: CardsProperties,
                  private val passwordEncoder: PasswordEncoder,
                  private val userTransactionService: UserTransactionService) {
    fun buyCard(userId: Int, buyCardTo: BuyCardTo): Card {
        val cardType = getCardType(buyCardTo.type)

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
        val buyCard = cardRepository.save(buyCardTo.asCard(userId, pinCode))
        if (userTransaction != null) {
            userTransactionService.addCardToCardTransaction(userTransaction, buyCard, cardType.price)
        }

        return buyCard
    }

    fun updateCard(userId: Int, cardId: String, adminUpdateCardTo: AdminUpdateCardTo): Card {
        val card = getCardById(cardId)
        CardUtils.checkCardOwner(userId, card)
        CardMapper.INSTANCE.updateCard(adminUpdateCardTo, card)
        return cardRepository.save(card)
    }

    fun updateCardPinCode(userId: Int, cardId: String, pinCode: String) {
        val card = getCardById(cardId)
        CardUtils.checkCardOwner(userId, card)
        val newPinCode = passwordEncoder.encode(pinCode)
        cardRepository.save(card.copy(pinCode = newPinCode))
    }

    fun updateCardPinCode(userId: Int, cardId: String, userUpdateCardPinCode: UserUpdateCardPinCode) {
        val card = getCardById(cardId)
        CardUtils.checkCardOwner(userId, card)
        if (!passwordEncoder.matches(userUpdateCardPinCode.oldPinCode, card.pinCode)) {
            throw FieldValidationException("oldPinCode", "Старый пин-код введен не верно!")
        }
        val newPinCode = passwordEncoder.encode(userUpdateCardPinCode.newPinCode)
        cardRepository.save(card.copy(pinCode = newPinCode))
    }

    fun createCard(adminCreateCardTo: AdminCreateCardTo): Card {
        val card = CardMapper.INSTANCE.asCard(adminCreateCardTo, passwordEncoder)
        return cardRepository.save(card)
    }

    fun getCardsByUserId(userId: Int): List<Card> {
        return cardRepository.findAllByUserId(userId)
    }

    fun getCardByNumberAndValidatePinCode(cardNumber: String, pinCode: String): Card {
        val card = getCardByNumber(cardNumber)
        if (!passwordEncoder.matches(pinCode, card.pinCode)) {
            throw AccessDeniedException("Incorrect pin code!")
        }
        return card
    }

    fun getCardByNumber(cardNumber: String): Card = cardRepository.findByNumber(cardNumber)
                .orElseThrow { NotFoundException("Card with number '$cardNumber' is not found!") }

    fun minusMoney(card: Card, price: Double, checkBalance: Boolean = true): Card {
        if (checkBalance && card.balance < price) {
            throw NotEnoughBalanceException("На карте недостаточно средств!")
        }

        return cardRepository.save(card.copy(balance = card.balance - price))
    }

    fun plusMoney(card: Card, price: Double): Card {
        return cardRepository.save(card.copy(balance = card.balance + price))
    }

    fun getCardById(cardId: String): Card = cardRepository.findById(cardId)
            .orElseThrow { NotFoundException("Card with id '$cardId' is not found!") }

    fun getCardType(type: String) = cardsProperties.cards[type]
            ?: throw NotFoundException("Card with code name '$type' is not found!")
}