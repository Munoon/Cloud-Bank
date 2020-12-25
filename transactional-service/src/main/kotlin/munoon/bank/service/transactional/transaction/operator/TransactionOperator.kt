package munoon.bank.service.transactional.transaction.operator

import munoon.bank.service.transactional.transaction.TransactionInfoData
import munoon.bank.service.transactional.transaction.UserTransaction
import munoon.bank.service.transactional.transaction.UserTransactionType
import javax.naming.OperationNotSupportedException

interface TransactionOperator {
    fun createTransaction(transactionInfoData: TransactionInfoData): UserTransaction

    fun createTransactionNextStep(userTransaction: UserTransaction, transactionInfoData: TransactionInfoData, step: Int): UserTransaction {
        throw OperationNotSupportedException("Transaction type ${userTransaction.type} does not support multiple step transaction creating")
    }

    fun cancel(userTransaction: UserTransaction, flags: Set<CancelTransactionFlag>): Boolean

    fun check(type: UserTransactionType): Boolean
}

enum class CancelTransactionFlag {
    DEACTIVATE_CARD
}