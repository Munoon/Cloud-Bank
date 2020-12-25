package munoon.bank.service.transactional.transaction

import munoon.bank.common.util.exception.ApplicationException
import munoon.bank.common.util.exception.NotFoundException
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
                             @Lazy private val cancelOperators: List<CancelTransactionOperator>,
                             @Lazy private val cardService: CardService) {
    fun buyCardTransaction(userId: Int, cardPrice: Double, cardDataTo: CardDataTo): UserTransaction {
        var price: Double
        val card = cardService.getCardByNumberAndValidatePinCode(cardDataTo.card, cardDataTo.pinCode).let {
            CardUtils.checkCardActive(it)
            CardUtils.checkCardOwner(userId, it)
            val type = cardService.getCardType(it.type)
            price = MoneyUtils.countWithTax(cardPrice, type.tax.other, TaxType.PLUS)
            cardService.minusMoney(it, price)
        }
        val transaction = UserTransaction(
                card = card,
                price = price,
                actualPrice = cardPrice,
                leftBalance = card.balance,
                type = UserTransactionType.CARD_BUY,
                registered = LocalDateTime.now(),
                canceled = false,
                id = null,
                info = null
        )
        return userTransactionRepository.save(transaction)
    }

    fun addCardToCardTransaction(userTransaction: UserTransaction, card: Card): UserTransaction {
        val newTransaction = userTransaction.copy(info = BuyCardUserTransactionInfo(card))
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
                    transactionInfo = AwardUserTransactionInfo(userId, fineAwardData.message)
                    count = MoneyUtils.countWithTax(fineAwardData.count, type.tax.fine, TaxType.MINUS)
                    cardService.plusMoney(it, count)
                }
                FineAwardType.FINE -> {
                    transactionType = UserTransactionType.FINE
                    transactionInfo = FineUserTransactionInfo(userId, fineAwardData.message)
                    count = MoneyUtils.countWithTax(fineAwardData.count, type.tax.award, TaxType.PLUS)
                    cardService.minusMoney(it, count, checkBalance = false)
                }
            }
        }
        val transaction = UserTransaction(
                card = card,
                price = count,
                actualPrice = fineAwardData.count,
                leftBalance = card.balance,
                type = transactionType,
                registered = LocalDateTime.now(),
                info = transactionInfo,
                canceled = false,
                id = null
        )
        return userTransactionRepository.save(transaction)
    }

    fun translateMoney(userId: Int, translateMoneyDataTo: TranslateMoneyDataTo): UserTransaction {
        var senderCard = cardService.getCardByNumberAndValidatePinCode(translateMoneyDataTo.cardData.card, translateMoneyDataTo.cardData.pinCode).also {
            CardUtils.checkCardActive(it)
            CardUtils.checkCardOwner(userId, it)
        }
        var receiverCard = cardService.getCardByNumber(translateMoneyDataTo.receiver).also {
            CardUtils.checkCardActive(it)
        }
        val card = cardService.getCardType(senderCard.type)

        val price = MoneyUtils.countWithTax(translateMoneyDataTo.count, card.tax.translate, TaxType.PLUS)

        senderCard = cardService.minusMoney(senderCard, price)
        receiverCard = cardService.plusMoney(receiverCard, translateMoneyDataTo.count)

        val senderTransaction = userTransactionRepository.save(UserTransaction(
                card = senderCard,
                price = price,
                actualPrice = translateMoneyDataTo.count,
                leftBalance = senderCard.balance,
                type = UserTransactionType.TRANSLATE_MONEY,
                registered = LocalDateTime.now(),
                info = null,
                canceled = false,
                id = null
        ))
        val receiverTransaction = userTransactionRepository.save(UserTransaction(
                card = receiverCard,
                price = translateMoneyDataTo.count,
                actualPrice = translateMoneyDataTo.count,
                leftBalance = receiverCard.balance,
                type = UserTransactionType.RECEIVE_MONEY,
                registered = LocalDateTime.now(),
                info = ReceiveUserTransactionInfo(senderTransaction.id!!, userId, translateMoneyDataTo.message),
                canceled = false,
                id = null
        ))
        val sendInfo = TranslateUserTransactionInfo(receiverTransaction.id!!, receiverCard.userId, translateMoneyDataTo.message)
        return userTransactionRepository.save(senderTransaction.copy(info = sendInfo))
    }

    fun cancelTransaction(transactionId: String, flags: Set<CancelTransactionFlag>): UserTransaction {
        val transaction = getTransaction(transactionId)
        if (transaction.canceled) {
            throw ApplicationException("Operation already canceled!")
        }
        val cancelOperator = (cancelOperators.find { it.check(transaction) }
                ?: throw NotFoundException("Cancel operator for transaction '${transactionId}' is not found!"))
        val result = cancelOperator.cancel(transaction, flags)
        val card = cardService.getCardById(transaction.card.id!!)
        if (result) {
            return userTransactionRepository.save(transaction.copy(canceled = true, card = card))
        }
        return transaction.copy(card = card)
    }

    fun getTransactions(cardId: String, userId: Int?, pageable: Pageable): Page<UserTransaction> {
        if (userId != null) {
            val card = cardService.getCardById(cardId)
            if (card.userId != userId) {
                throw AccessDeniedException("That card belong to another user")
            }
        }
        return userTransactionRepository.getAllByCardId(cardId, pageable)
    }

    fun getTransaction(transactionId: String) = userTransactionRepository.findById(transactionId)
            .orElseThrow { NotFoundException("Transaction with id '$transactionId' is not found!") }

    fun getAll(transactionsId: Set<String>) = userTransactionRepository.findAllById(transactionsId)
            .map { it.id!! to it }
            .toMap()
}