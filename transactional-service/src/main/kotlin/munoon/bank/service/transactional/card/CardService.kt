package munoon.bank.service.transactional.card

import munoon.bank.common.util.exception.FieldValidationException
import munoon.bank.common.util.exception.NotFoundException
import munoon.bank.service.transactional.transaction.AddCardTransactionInfoData
import munoon.bank.service.transactional.transaction.BuyCardTransactionInfoData
import munoon.bank.service.transactional.transaction.UserTransactionService
import munoon.bank.service.transactional.util.NotEnoughBalanceException
import munoon.bank.service.transactional.util.checkActive
import munoon.bank.service.transactional.util.checkOwner
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class CardService(private val cardRepository: CardRepository,
                  private val cardsProperties: CardsProperties,
                  private val passwordEncoder: PasswordEncoder,
                  private val cardMapper: CardMapper,
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
            else -> userTransactionService.makeTransaction(BuyCardTransactionInfoData(userId, cardType.price, buyCardTo.cardData
                    ?: throw AccessDeniedException("You should specify card for doing this operation.")))
        }

        val cardPrimary = cardRepository.countAllByUserId(userId) == 0
        val card = cardMapper.asCard(buyCardTo, userId, cardPrimary).let { cardRepository.save(it) }
        if (userTransaction != null) {
            userTransactionService.makeTransactionNextStep(userTransaction, AddCardTransactionInfoData(card), 1)
        }

        return card
    }

    fun updateCard(userId: Int, cardId: String, adminUpdateCardTo: AdminUpdateCardTo): Card {
        val card = getCardById(cardId).checkOwner(userId)
        cardMapper.updateCard(adminUpdateCardTo, card)
        return cardRepository.save(card)
    }

    fun updateCardPinCode(userId: Int, cardId: String, pinCode: String) {
        val card = getCardById(cardId).checkOwner(userId)
        val newPinCode = passwordEncoder.encode(pinCode)
        cardRepository.save(card.copy(pinCode = newPinCode))
    }

    fun updateCardPinCode(userId: Int, cardId: String, userUpdateCardPinCode: UserUpdateCardPinCode) {
        val card = getCardById(cardId).checkOwner(userId)
        if (!passwordEncoder.matches(userUpdateCardPinCode.oldPinCode, card.pinCode)) {
            throw FieldValidationException("oldPinCode", "Старый пин-код введен не верно!")
        }
        val newPinCode = passwordEncoder.encode(userUpdateCardPinCode.newPinCode)
        cardRepository.save(card.copy(pinCode = newPinCode))
    }

    fun changePrimaryCard(userId: Int, cardId: String) {
        val card = getCardById(cardId)
                .checkOwner(userId)
                .checkActive()
        cardRepository.makeAllUnPrimaryByUserId(userId)
        cardRepository.save(card.copy(primary = true))
    }

    fun createCard(adminCreateCardTo: AdminCreateCardTo): Card {
        val primary = cardRepository.countAllByUserId(adminCreateCardTo.userId) == 0
        val card = cardMapper.asCard(adminCreateCardTo, primary)
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

    fun deactivateAllByOwner(userId: Int) {
        cardRepository.deactivateAllByUserId(userId)
    }

    fun deactivateCard(cardId: String) {
        val result = cardRepository.deactivateCard(cardId)
        if (result.modifiedCount == 0L) {
            throw NotFoundException("Card with id '$cardId' is not found!")
        }
    }

    fun getPrimaryCardByUserId(userId: Int): Card = cardRepository.findByUserIdAndPrimaryTrue(userId)
            .orElseThrow { NotFoundException("User $userId haven't primary card") }
}