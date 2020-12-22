package munoon.bank.service.transactional.transaction

import munoon.bank.service.transactional.card.CardService
import org.springframework.stereotype.Component

interface CancelTransactionOperator {
    /**
     * Cancel operation
     * @return flag is successful. If true - transaction will be marked as canceled.
     */
    fun cancel(userTransaction: UserTransaction): Boolean

    /**
     * Return flag if operation supported
     */
    fun check(userTransaction: UserTransaction): Boolean
}

@Component
class AwardCancelTransactionOperator(private val cardService: CardService) : CancelTransactionOperator {
    override fun cancel(userTransaction: UserTransaction): Boolean {
        cardService.minusMoney(userTransaction.card, userTransaction.price, checkBalance = false)
        return true
    }

    override fun check(userTransaction: UserTransaction) = userTransaction.type == UserTransactionType.AWARD
}

@Component
class FineCancelTransactionOperator(private val cardService: CardService) : CancelTransactionOperator {
    override fun cancel(userTransaction: UserTransaction): Boolean {
        cardService.plusMoney(userTransaction.card, userTransaction.price)
        return true
    }

    override fun check(userTransaction: UserTransaction) = userTransaction.type == UserTransactionType.FINE
}