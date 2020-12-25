package munoon.bank.service.transactional.transaction

import munoon.bank.common.util.exception.ApplicationException
import munoon.bank.common.util.exception.NotFoundException
import munoon.bank.service.transactional.card.CardService
import munoon.bank.service.transactional.transaction.operator.CancelTransactionFlag
import munoon.bank.service.transactional.transaction.operator.TransactionOperator
import org.springframework.context.annotation.Lazy
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.util.Assert

@Service
class UserTransactionService(private val userTransactionRepository: UserTransactionRepository,
                             @Lazy private val transactionOperators: List<TransactionOperator>,
                             @Lazy private val cardService: CardService) {
    fun makeTransaction(transactionInfoData: TransactionInfoData): UserTransaction {
        val type = transactionInfoData.transactionType
        val operator = transactionOperators.find { it.check(type) }
                ?: throw NotFoundException("Operator for transaction type $type is not found!")
        return operator.createTransaction(transactionInfoData)
    }

    fun makeTransactionNextStep(userTransaction: UserTransaction,
                                transactionInfoData: TransactionInfoData,
                                step: Int): UserTransaction {
        val type = transactionInfoData.transactionType
        val operator = transactionOperators.find { it.check(type) }
                ?: throw NotFoundException("Operator for transaction type $type is not found!")
        return operator.createTransactionNextStep(userTransaction, transactionInfoData, step)
    }

    fun cancelTransaction(transactionId: String, flags: Set<CancelTransactionFlag>): UserTransaction {
        val transaction = getTransaction(transactionId)
        if (transaction.canceled) {
            throw ApplicationException("Operation already canceled!")
        }
        val cancelOperator = transactionOperators.find { it.check(transaction.type) }
                ?: throw NotFoundException("Cancel operator for transaction '$transactionId' is not found!")
        val result = cancelOperator.cancel(transaction, flags)
        val card = cardService.getCardById(transaction.card.id!!)
        if (result) {
            return userTransactionRepository.save(transaction.copy(canceled = true, card = card))
        }
        return transaction.copy(card = card)
    }

    fun getTransactions(cardId: String, userId: Int?, pageable: Pageable): Page<UserTransaction> {
        if (userId != null) {
            val card = cardService.getCardById(cardId)
            if (card.userId != userId) {
                throw AccessDeniedException("That card belong to another user")
            }
        }
        return userTransactionRepository.getAllByCardId(cardId, pageable)
    }

    fun getTransaction(transactionId: String) = userTransactionRepository.findById(transactionId)
            .orElseThrow { NotFoundException("Transaction with id '$transactionId' is not found!") }

    fun getAll(transactionsId: Set<String>) = userTransactionRepository.findAllById(transactionsId)
            .map { it.id!! to it }
            .toMap()

    fun create(transaction: UserTransaction): UserTransaction {
        Assert.isNull(transaction.id, "Transaction id must be null")
        return userTransactionRepository.save(transaction)
    }

    fun update(userTransaction: UserTransaction): UserTransaction {
        Assert.notNull(userTransaction.id, "User transaction is new!")
        return userTransactionRepository.save(userTransaction)
    }
}