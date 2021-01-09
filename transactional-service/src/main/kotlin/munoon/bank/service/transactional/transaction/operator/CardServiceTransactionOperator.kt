package munoon.bank.service.transactional.transaction.operator

import munoon.bank.service.transactional.card.CardService
import munoon.bank.service.transactional.transaction.*
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CardServiceTransactionOperator(private val cardService: CardService,
                                     private val transactionService: UserTransactionService) : TransactionOperator {
    override fun createTransaction(transactionInfoData: TransactionInfoData): UserTransaction? {
        val info = transactionInfoData as CardServiceTransactionInfoData
        var card = cardService.getCardById(info.cardId)
        val cardType = cardService.getCardType(card.type)
        card = cardService.minusMoney(card, cardType.service, checkBalance = false)
        return transactionService.create(UserTransaction(
                card = card,
                price = cardType.service,
                actualPrice = cardType.service,
                leftBalance = card.balance,
                type = UserTransactionType.CARD_SERVICE,
                registered = LocalDateTime.now(),
                canceled = false,
                id = null,
                info = null
        ))
    }

    override fun cancel(userTransaction: UserTransaction, flags: Set<CancelTransactionFlag>): Boolean {
        cardService.plusMoney(userTransaction.card, userTransaction.price)
        return true
    }

    override fun check(type: UserTransactionType) = type == UserTransactionType.CARD_SERVICE
}