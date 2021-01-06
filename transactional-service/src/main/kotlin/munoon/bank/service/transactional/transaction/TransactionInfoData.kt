package munoon.bank.service.transactional.transaction

import munoon.bank.service.transactional.card.Card
import munoon.bank.service.transactional.card.CardDataTo

interface TransactionInfoData {
    val transactionType: UserTransactionType
}

data class FineAwardTransactionInfoData(
        val userId: Int,
        val fineAwardData: FineAwardDataTo
) : TransactionInfoData {
    override val transactionType = fineAwardData.type.originalType
}

data class BuyCardTransactionInfoData(
        val userId: Int,
        val cardPrice: Double,
        val cardDataTo: CardDataTo
) : TransactionInfoData {
    override val transactionType = UserTransactionType.CARD_BUY
}

data class AddCardTransactionInfoData(val card: Card) : TransactionInfoData {
    override val transactionType = UserTransactionType.CARD_BUY
}

data class TranslateMoneyTransactionInfoData(
        val userId: Int,
        val translateMoneyDataTo: TranslateMoneyDataTo
) : TransactionInfoData {
    override val transactionType = UserTransactionType.TRANSLATE_MONEY
}

data class SalaryTransactionInfoData(
        val userId: Int,
        val count: Double
) : TransactionInfoData {
    override val transactionType = UserTransactionType.SALARY
}