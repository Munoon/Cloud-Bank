package munoon.bank.service.transactional.transaction.operator

import munoon.bank.common.util.exception.NotFoundException
import munoon.bank.service.transactional.card.CardService
import munoon.bank.service.transactional.transaction.*
import munoon.bank.service.transactional.util.MoneyUtils
import munoon.bank.service.transactional.util.checkActive
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class SalaryTransactionOperator(private val cardService: CardService,
                                private val transactionService: UserTransactionService) : TransactionOperator {
    override fun createTransaction(transactionInfoData: TransactionInfoData): UserTransaction? {
        val info = transactionInfoData as SalaryTransactionInfoData
        var card = try {
            cardService.getPrimaryCardByUserId(info.userId)
        } catch (e: NotFoundException) {
            return null
        }
        card.checkActive()
        val type = cardService.getCardType(card.type)
        val price = MoneyUtils.countWithTax(info.count, type.tax.salary, MoneyUtils.TaxType.MINUS)
        card = cardService.plusMoney(card, price)
        return transactionService.create(UserTransaction(
                card = card,
                price = price,
                actualPrice = info.count,
                leftBalance = card.balance,
                type = UserTransactionType.SALARY,
                registered = LocalDateTime.now(),
                canceled = false,
                id = null,
                info = null
        ))
    }

    override fun cancel(userTransaction: UserTransaction, flags: Set<CancelTransactionFlag>): Boolean {
        cardService.minusMoney(userTransaction.card, userTransaction.price, checkBalance = false)
        return true
    }

    override fun check(type: UserTransactionType) = type == UserTransactionType.SALARY
}