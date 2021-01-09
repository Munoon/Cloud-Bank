package munoon.bank.service.transactional.transaction

import munoon.bank.common.transaction.to.SalaryUserTransactionInfoTo
import munoon.bank.common.user.UserTo
import munoon.bank.service.transactional.card.CardMapper
import munoon.bank.service.transactional.card.CardToWithOwner
import munoon.bank.service.transactional.card.SafeCardToWithOwner
import munoon.bank.service.transactional.user.UserService
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page

@Mapper(componentModel = "spring")
abstract class UserTransactionMapper {
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userTransactionService: UserTransactionService

    @Autowired
    protected lateinit var cardMapper: CardMapper

    @Mapping(target = "card", expression = "java(cardMapper.asTo(userTransaction.getCard()))")
    abstract fun asPaySalaryTo(userTransaction: UserTransaction): SalaryUserTransactionInfoTo

    // To UserTransactionTo with unsafe info

    fun asToWithUnSafeInfo(userTransactions: Page<UserTransaction>): Page<UserTransactionTo> {
        val usersMap = getUsersMap(userTransactions.content)
        val transactionsMap = getTransactionsMap(userTransactions.content)
        return userTransactions.map { asToWithUnSafeInfo(it, usersMap, transactionsMap) }
    }

    fun asToWithUnSafeInfo(userTransaction: UserTransaction): UserTransactionTo {
        val usersMap = getUsersMap(userTransaction)
        val transactionsMap = getTransactionsMap(userTransaction)
        return asToWithUnSafeInfo(userTransaction, usersMap, transactionsMap)
    }

    @Mappings(
            Mapping(target = "info", expression = "java(mapUnSafeInfo(userTransaction, users, transactions))"),
            Mapping(target = "card", expression = "java(mapUnSafeCard(userTransaction, users))")
    )
    abstract fun asToWithUnSafeInfo(userTransaction: UserTransaction,
                                    users: Map<Int, UserTo?>,
                                    transactions: Map<String, UserTransaction>): UserTransactionTo

    @Mappings(
            Mapping(target = "info", expression = "java(null)"),
            Mapping(target = "card", expression = "java(mapUnSafeCard(userTransaction, users))")
    )
    abstract fun asToIgnoreInfo(userTransaction: UserTransaction, users: Map<Int, UserTo?>): UserTransactionTo

    // To SafeUserTransactionTo

    fun asToWithSafeInfo(userTransaction: Page<UserTransaction>): Page<UserTransactionTo> {
        val usersMap = getUsersMap(userTransaction.content)
        val transactionsMap = getTransactionsMap(userTransaction.content)
        return userTransaction.map { asToWithSafeInfo(it, usersMap, transactionsMap) }
    }

    fun asToWithSafeInfo(userTransaction: UserTransaction): UserTransactionTo {
        val usersMap = getUsersMap(userTransaction)
        val transactionsMap = getTransactionsMap(userTransaction)
        return asToWithSafeInfo(userTransaction, usersMap, transactionsMap)
    }

    @Mappings(
            Mapping(target = "info", expression = "java(mapSafeInfo(userTransaction, users, transactions))"),
            Mapping(target = "card", expression = "java(mapUnSafeCard(userTransaction, users))")
    )
    abstract fun asToWithSafeInfo(userTransaction: UserTransaction,
                                  users: Map<Int, UserTo?>,
                                  transactions: Map<String, UserTransaction>): UserTransactionTo

    @Mappings(
            Mapping(target = "info", expression = "java(null)"),
            Mapping(target = "card", expression = "java(mapSafeCard(userTransaction, users))")
    )
    abstract fun asSafeToIgnoreInfo(userTransaction: UserTransaction, users: Map<Int, UserTo?>): SafeUserTransactionTo

    // Mappers

    protected fun mapUnSafeCard(userTransaction: UserTransaction, users: Map<Int, UserTo?>): CardToWithOwner {
        val user = users[userTransaction.card.userId]
        return cardMapper.asTo(userTransaction.card, user)
    }

    protected fun mapSafeCard(userTransaction: UserTransaction, users: Map<Int, UserTo?>): SafeCardToWithOwner {
        val user = users[userTransaction.card.userId]
        return cardMapper.asSafeTo(userTransaction.card, user)
    }

    protected fun mapUnSafeInfo(userTransaction: UserTransaction,
                                users: Map<Int, UserTo?>,
                                transactions: Map<String, UserTransaction>): UnSafeUserTransactionInfoTo? =
            when (userTransaction.info) {
                is BuyCardUserTransactionInfo -> asTo(userTransaction.info)
                is AwardUserTransactionInfo -> asTo(userTransaction.info, users)
                is FineUserTransactionInfo -> asTo(userTransaction.info, users)
                is TranslateUserTransactionInfo -> asTo(userTransaction.info, users, transactions)
                is ReceiveUserTransactionInfo -> asTo(userTransaction.info, users, transactions)
                null -> null
            }

    protected fun mapSafeInfo(userTransaction: UserTransaction,
                          users: Map<Int, UserTo?>,
                          transactions: Map<String, UserTransaction>): SafeUserTransactionInfoTo? {
        val unSafeUserTransactionInfoTo = mapUnSafeInfo(userTransaction, users, transactions)
        if (unSafeUserTransactionInfoTo is SafeUserTransactionInfoTo) {
            return unSafeUserTransactionInfoTo
        }
        return when (userTransaction.info) {
            is TranslateUserTransactionInfo -> asSafeTo(userTransaction.info, users, transactions)
            is ReceiveUserTransactionInfo -> asSafeTo(userTransaction.info, users, transactions)
            else -> null
        }
    }

    // Info to InfoTo

    protected abstract fun asTo(buyCardUserTransactionInfo: BuyCardUserTransactionInfo): BuyCardUserTransactionInfoTo

    @Mapping(target = "user", expression = "java(users.get(awardUserTransactionInfo.getUserId()))")
    protected abstract fun asTo(awardUserTransactionInfo: AwardUserTransactionInfo, users: Map<Int, UserTo?>): AwardUserTransactionInfoTo

    @Mapping(target = "user", expression = "java(users.get(fineUserTransactionInfo.getUserId()))")
    protected abstract fun asTo(fineUserTransactionInfo: FineUserTransactionInfo, users: Map<Int, UserTo?>): FineUserTransactionInfoTo

    protected fun asTo(translateUserTransactionInfo: TranslateUserTransactionInfo,
                       users: Map<Int, UserTo?>,
                       transactions: Map<String, UserTransaction>): TranslateUserTransactionInfoTo {
        val transaction = transactions[translateUserTransactionInfo.receiveTransactionId]!!
        val receiveTransaction = asToIgnoreInfo(transaction, users)
        return TranslateUserTransactionInfoTo(receiveTransaction, translateUserTransactionInfo.message)
    }

    protected fun asTo(receiveUserTransactionInfo: ReceiveUserTransactionInfo,
                       users: Map<Int, UserTo?>,
                       transactions: Map<String, UserTransaction>): ReceiveUserTransactionInfoTo {
        val transaction = transactions[receiveUserTransactionInfo.translateTransactionId]!!
        val translateTransaction = asToIgnoreInfo(transaction, users)
        return ReceiveUserTransactionInfoTo(translateTransaction, receiveUserTransactionInfo.message)
    }

    // Info to SafeInfoTo

    protected fun asSafeTo(translateUserTransactionInfo: TranslateUserTransactionInfo,
                           users: Map<Int, UserTo?>,
                           transactions: Map<String, UserTransaction>): SafeTranslateUserTransactionInfoTo {
        val transaction = transactions[translateUserTransactionInfo.receiveTransactionId]!!
        val receiveTransaction = asSafeToIgnoreInfo(transaction, users)
        return SafeTranslateUserTransactionInfoTo(receiveTransaction, translateUserTransactionInfo.message)
    }

    protected fun asSafeTo(receiveUserTransactionInfo: ReceiveUserTransactionInfo,
                           users: Map<Int, UserTo?>,
                           transactions: Map<String, UserTransaction>): SafeReceiveUserTransactionInfoTo {
        val transaction = transactions[receiveUserTransactionInfo.translateTransactionId]!!
        val translateTransaction = asSafeToIgnoreInfo(transaction, users)
        return SafeReceiveUserTransactionInfoTo(translateTransaction, receiveUserTransactionInfo.message)
    }

    // Utils

    private fun getUsersMap(userTransaction: UserTransaction) = getUsersMap(listOf(userTransaction))
    private fun getTransactionsMap(userTransaction: UserTransaction) = getTransactionsMap(listOf(userTransaction))

    private fun getUsersMap(userTransactions: List<UserTransaction>): Map<Int, UserTo?> {
        val users = userTransactions.map { it.getUsersId() }.flatten().toSet()
        return if (users.isNotEmpty()) userService.getUsersById(users) else emptyMap()
    }

    private fun getTransactionsMap(userTransactions: List<UserTransaction>): Map<String, UserTransaction> {
        val transactions = userTransactions
                .mapNotNull { if (it.info is UserTransactionsIdCollector) it.info.getTransactionsId() else null }
                .flatten()
                .toSet()
        return if (transactions.isNotEmpty()) userTransactionService.getAll(transactions) else emptyMap()
    }
}