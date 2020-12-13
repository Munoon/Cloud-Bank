package munoon.bank.service.transactional.transaction

import munoon.bank.service.transactional.card.Card
import munoon.bank.service.transactional.card.CardDataTo
import munoon.bank.service.transactional.card.CardService
import munoon.bank.service.transactional.util.CardUtils
import munoon.bank.service.transactional.util.MoneyUtils
import munoon.bank.service.transactional.util.MoneyUtils.TaxType
import org.springframework.context.annotation.Lazy
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserTransactionService(private val userTransactionRepository: UserTransactionRepository,
                             @Lazy private val cardService: CardService) {
    fun buyCardTransaction(userId: Int, cardPrice: Double, cardDataTo: CardDataTo): UserTransaction {
        var price: Double
        val card = cardService.getCardByNumberAndValidatePinCode(cardDataTo.card, cardDataTo.pinCode).let {
            CardUtils.checkCardActive(it)
            CardUtils.checkCardOwner(userId, it)
            val type = cardService.getCardType(it.type)
            price = MoneyUtils.countWithTax(cardPrice, type.otherTax, TaxType.PLUS)
            cardService.minusMoney(it, price)
        }
        val transaction = UserTransaction(null, card, price, card.balance, LocalDateTime.now(), UserTransactionType.CARD_BUY, null)
        return userTransactionRepository.save(transaction)
    }

    fun addCardToCardTransaction(userTransaction: UserTransaction, card: Card, cardPrice: Double): UserTransaction {
        val info = BuyCardUserTransactionInfo(card, cardPrice)
        val newTransaction = userTransaction.copy(info = info)
        return userTransactionRepository.save(newTransaction)
    }

    fun fineAwardTransaction(userId: Int, fineAwardData: FineAwardDataTo): UserTransaction {
        var transactionType: UserTransactionType
        var transactionInfo: UserTransactionInfo
        var count: Double
        val card = cardService.getCardByNumber(fineAwardData.card).let {
            CardUtils.checkCardActive(it)
            val type = cardService.getCardType(it.type)
            when (fineAwardData.type) {
                FineAwardType.AWARD -> {
                    transactionType = UserTransactionType.AWARD
                    transactionInfo = AwardUserTransactionInfo(userId, fineAwardData.message, fineAwardData.count)
                    count = MoneyUtils.countWithTax(fineAwardData.count, type.awardTax, TaxType.MINUS)
                    cardService.plusMoney(it, count)
                }
                FineAwardType.FINE -> {
                    transactionType = UserTransactionType.FINE
                    transactionInfo = FineUserTransactionInfo(userId, fineAwardData.message, fineAwardData.count)
                    count = MoneyUtils.countWithTax(fineAwardData.count, type.fineTax, TaxType.PLUS)
                    cardService.minusMoney(it, count, checkBalance = false)
                }
            }
        }
        val transaction = UserTransaction(null, card, count, card.balance, LocalDateTime.now(), transactionType, transactionInfo)
        return userTransactionRepository.save(transaction)
    }

    fun getTransactions(cardId: String, userId: Int, pageable: Pageable): Page<UserTransaction> {
        val card = cardService.getCardById(cardId)
        if (card.userId != userId) {
            throw AccessDeniedException("That card belong to another user")
        }
        return userTransactionRepository.getAllByCardId(cardId, pageable)
    }
}