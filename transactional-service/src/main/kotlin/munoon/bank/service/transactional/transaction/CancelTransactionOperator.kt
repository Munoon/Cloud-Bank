package munoon.bank.service.transactional.transaction

import munoon.bank.service.transactional.card.CardService
import org.springframework.stereotype.Component

interface CancelTransactionOperator {
    /**
     * Cancel operation
     * @return flag is successful. If true - transaction will be marked as canceled.
     */
    fun cancel(userTransaction: UserTransaction, flags: Set<CancelTransactionFlag>): Boolean

    /**
     * Return flag if operation supported
     */
    fun check(userTransaction: UserTransaction): Boolean
}

enum class CancelTransactionFlag {
    DEACTIVATE_CARD
}

@Component
class AwardCancelTransactionOperator(private val cardService: CardService) : CancelTransactionOperator {
    override fun cancel(userTransaction: UserTransaction, flags: Set<CancelTransactionFlag>): Boolean {
        cardService.minusMoney(userTransaction.card, userTransaction.price, checkBalance = false)
        return true
    }

    override fun check(userTransaction: UserTransaction) = userTransaction.type == UserTransactionType.AWARD
}

@Component
class FineCancelTransactionOperator(private val cardService: CardService) : CancelTransactionOperator {
    override fun cancel(userTransaction: UserTransaction, flags: Set<CancelTransactionFlag>): Boolean {
        cardService.plusMoney(userTransaction.card, userTransaction.price)
        return true
    }

    override fun check(userTransaction: UserTransaction) = userTransaction.type == UserTransactionType.FINE
}

@Component
class CancelBuyCardTransactionOperator(private val cardService: CardService) : CancelTransactionOperator {
    override fun cancel(userTransaction: UserTransaction, flags: Set<CancelTransactionFlag>): Boolean {
        cardService.plusMoney(userTransaction.card, userTransaction.price)
        if (flags.contains(CancelTransactionFlag.DEACTIVATE_CARD) && userTransaction.info is BuyCardUserTransactionInfo) {
            val boughtCardId = userTransaction.info.buyCard.id!!
            cardService.deactivateCard(boughtCardId)
        }
        return true
    }

    override fun check(userTransaction: UserTransaction) = userTransaction.type == UserTransactionType.CARD_BUY
}

@Component
class CancelTranslateTransactionOperation(private val cardService: CardService,
                                          private var transactionService: UserTransactionService) : CancelTransactionOperator {
    override fun cancel(userTransaction: UserTransaction, flags: Set<CancelTransactionFlag>): Boolean {
        val translateTransaction: UserTransaction
        val receiveTransaction: UserTransaction
        when (userTransaction.type) {
            UserTransactionType.TRANSLATE_MONEY -> {
                translateTransaction = userTransaction
                val receiveTransactionId = (userTransaction.info as TranslateUserTransactionInfo).receiveTransactionId
                receiveTransaction = transactionService.getTransaction(receiveTransactionId)
            }
            UserTransactionType.RECEIVE_MONEY -> {
                receiveTransaction = userTransaction
                val translateTransactionId = (userTransaction.info as ReceiveUserTransactionInfo).translateTransactionId
                translateTransaction = transactionService.getTransaction(translateTransactionId)
            }
            else -> {
                translateTransaction = userTransaction
                receiveTransaction = userTransaction
            }
        }
        cardService.plusMoney(translateTransaction.card, translateTransaction.price)
        cardService.minusMoney(receiveTransaction.card, receiveTransaction.price)
        return true
    }

    override fun check(userTransaction: UserTransaction) = userTransaction.type == UserTransactionType.TRANSLATE_MONEY
            || userTransaction.type == UserTransactionType.RECEIVE_MONEY
}