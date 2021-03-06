package munoon.bank.service.transactional.transaction

import munoon.bank.service.transactional.card.Card
import munoon.bank.service.transactional.card.CardDataTo

abstract class TransactionInfoData(val transactionType: UserTransactionType)

data class FineAwardTransactionInfoData(
        val userId: Int,
        val fineAwardData: FineAwardDataTo
) : TransactionInfoData(fineAwardData.type.originalType)

data class BuyCardTransactionInfoData(
        val userId: Int,
        val cardPrice: Double,
        val cardDataTo: CardDataTo
) : TransactionInfoData(UserTransactionType.CARD_BUY)

data class AddCardTransactionInfoData(val card: Card) : TransactionInfoData(UserTransactionType.CARD_BUY)

data class TranslateMoneyTransactionInfoData(
        val userId: Int,
        val translateMoneyDataTo: TranslateMoneyDataTo
) : TransactionInfoData(UserTransactionType.TRANSLATE_MONEY)

data class SalaryTransactionInfoData(
        val userId: Int,
        val count: Double
) : TransactionInfoData(UserTransactionType.SALARY)

data class CardServiceTransactionInfoData(
        val cardId: String
) : TransactionInfoData(UserTransactionType.CARD_SERVICE)