package munoon.bank.service.transactional.transaction.operator

import munoon.bank.service.transactional.card.CardService
import munoon.bank.service.transactional.transaction.*
import munoon.bank.service.transactional.util.CardUtils
import munoon.bank.service.transactional.util.MoneyUtils
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class BuyCardTransactionOperator(private val cardService: CardService,
                                 private val transactionService: UserTransactionService) : TransactionOperator {
    override fun createTransaction(transactionInfoData: TransactionInfoData): UserTransaction {
        val info = transactionInfoData as BuyCardTransactionInfoData
        var price: Double
        val card = cardService.getCardByNumberAndValidatePinCode(info.cardDataTo.card, info.cardDataTo.pinCode).let {
            CardUtils.checkCardActive(it)
            CardUtils.checkCardOwner(info.userId, it)
            val type = cardService.getCardType(it.type)
            price = MoneyUtils.countWithTax(info.cardPrice, type.tax.other, MoneyUtils.TaxType.PLUS)
            cardService.minusMoney(it, price)
        }
        val transaction = UserTransaction(
                card = card,
                price = price,
                actualPrice = info.cardPrice,
                leftBalance = card.balance,
                type = UserTransactionType.CARD_BUY,
                registered = LocalDateTime.now(),
                canceled = false,
                id = null,
                info = null
        )
        return transactionService.create(transaction)
    }

    override fun createTransactionNextStep(userTransaction: UserTransaction, transactionInfoData: TransactionInfoData, step: Int): UserTransaction {
        if (step != 1) {
            throw IllegalArgumentException("The step 1 is only supported")
        }
        val card = (transactionInfoData as AddCardTransactionInfoData).card
        val newTransaction = userTransaction.copy(info = BuyCardUserTransactionInfo(card))
        return transactionService.update(newTransaction)
    }

    override fun cancel(userTransaction: UserTransaction, flags: Set<CancelTransactionFlag>): Boolean {
        cardService.plusMoney(userTransaction.card, userTransaction.price)
        if (flags.contains(CancelTransactionFlag.DEACTIVATE_CARD) && userTransaction.info is BuyCardUserTransactionInfo) {
            val boughtCardId = userTransaction.info.buyCard.id!!
            cardService.deactivateCard(boughtCardId)
        }
        return true
    }

    override fun check(type: UserTransactionType) = type == UserTransactionType.CARD_BUY
}