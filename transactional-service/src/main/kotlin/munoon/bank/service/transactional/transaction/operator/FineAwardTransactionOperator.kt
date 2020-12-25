package munoon.bank.service.transactional.transaction.operator

import munoon.bank.service.transactional.card.CardService
import munoon.bank.service.transactional.transaction.*
import munoon.bank.service.transactional.util.CardUtils
import munoon.bank.service.transactional.util.MoneyUtils
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class FineAwardTransactionOperator(private val cardService: CardService,
                                   private val transactionService: UserTransactionService) : TransactionOperator {
    override fun createTransaction(transactionInfoData: TransactionInfoData): UserTransaction {
        val info = transactionInfoData as FineAwardTransactionInfoData
        var transactionType: UserTransactionType
        var transactionInfo: UserTransactionInfo
        var count: Double
        val card = cardService.getCardByNumber(info.fineAwardData.card).let {
            CardUtils.checkCardActive(it)
            val type = cardService.getCardType(it.type)
            when (info.fineAwardData.type) {
                FineAwardType.AWARD -> {
                    transactionType = UserTransactionType.AWARD
                    transactionInfo = AwardUserTransactionInfo(info.userId, info.fineAwardData.message)
                    count = MoneyUtils.countWithTax(info.fineAwardData.count, type.tax.fine, MoneyUtils.TaxType.MINUS)
                    cardService.plusMoney(it, count)
                }
                FineAwardType.FINE -> {
                    transactionType = UserTransactionType.FINE
                    transactionInfo = FineUserTransactionInfo(info.userId, info.fineAwardData.message)
                    count = MoneyUtils.countWithTax(info.fineAwardData.count, type.tax.award, MoneyUtils.TaxType.PLUS)
                    cardService.minusMoney(it, count, checkBalance = false)
                }
            }
        }
        val transaction = UserTransaction(
                card = card,
                price = count,
                actualPrice = info.fineAwardData.count,
                leftBalance = card.balance,
                type = transactionType,
                registered = LocalDateTime.now(),
                info = transactionInfo,
                canceled = false,
                id = null
        )
        return transactionService.create(transaction)
    }

    override fun cancel(userTransaction: UserTransaction, flags: Set<CancelTransactionFlag>): Boolean {
        when (userTransaction.type) {
            UserTransactionType.AWARD -> cardService.minusMoney(userTransaction.card, userTransaction.price, checkBalance = false)
            UserTransactionType.FINE -> cardService.plusMoney(userTransaction.card, userTransaction.price)
            else -> {}
        }
        return true
    }

    override fun check(type: UserTransactionType) = type == UserTransactionType.AWARD
            || type == UserTransactionType.FINE
}