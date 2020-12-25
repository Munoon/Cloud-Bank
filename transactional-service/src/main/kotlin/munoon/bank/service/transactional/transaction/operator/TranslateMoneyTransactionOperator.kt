package munoon.bank.service.transactional.transaction.operator

import munoon.bank.service.transactional.card.CardService
import munoon.bank.service.transactional.transaction.*
import munoon.bank.service.transactional.util.MoneyUtils
import munoon.bank.service.transactional.util.checkActive
import munoon.bank.service.transactional.util.checkOwner
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class TranslateMoneyTransactionOperator(private val cardService: CardService,
                                        private val transactionService: UserTransactionService) : TransactionOperator {
    override fun createTransaction(transactionInfoData: TransactionInfoData): UserTransaction {
        val info = transactionInfoData as TranslateMoneyTransactionInfoData
        val senderCardNumber = info.translateMoneyDataTo.cardData.card
        val senderCardPinCode = info.translateMoneyDataTo.cardData.pinCode
        var senderCard = cardService.getCardByNumberAndValidatePinCode(senderCardNumber, senderCardPinCode)
                .checkActive()
                .checkOwner(info.userId)
        var receiverCard = cardService.getCardByNumber(info.translateMoneyDataTo.receiver).checkActive()
        val card = cardService.getCardType(senderCard.type)

        val count = info.translateMoneyDataTo.count
        val price = MoneyUtils.countWithTax(count, card.tax.translate, MoneyUtils.TaxType.PLUS)

        senderCard = cardService.minusMoney(senderCard, price)
        receiverCard = cardService.plusMoney(receiverCard, count)

        val senderTransaction = transactionService.create(UserTransaction(
                card = senderCard,
                price = price,
                actualPrice = count,
                leftBalance = senderCard.balance,
                type = UserTransactionType.TRANSLATE_MONEY,
                registered = LocalDateTime.now(),
                info = null,
                canceled = false,
                id = null
        ))
        val message = info.translateMoneyDataTo.message
        val receiverTransaction = transactionService.create(UserTransaction(
                card = receiverCard,
                price = count,
                actualPrice = count,
                leftBalance = receiverCard.balance,
                type = UserTransactionType.RECEIVE_MONEY,
                registered = LocalDateTime.now(),
                info = ReceiveUserTransactionInfo(senderTransaction.id!!, info.userId, message),
                canceled = false,
                id = null
        ))
        val sendInfo = TranslateUserTransactionInfo(receiverTransaction.id!!, receiverCard.userId, message)
        return transactionService.update(senderTransaction.copy(info = sendInfo))
    }

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

    override fun check(type: UserTransactionType) = type == UserTransactionType.TRANSLATE_MONEY
            || type == UserTransactionType.RECEIVE_MONEY
}