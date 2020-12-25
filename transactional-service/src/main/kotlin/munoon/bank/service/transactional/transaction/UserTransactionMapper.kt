package munoon.bank.service.transactional.transaction

import munoon.bank.common.user.UserTo
import munoon.bank.service.transactional.card.CardMapper
import munoon.bank.service.transactional.card.CardToWithOwner
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

    fun asTo(userTransactions: Page<UserTransaction>): Page<UserTransactionTo> {
        val users = HashSet<Int>()
        val transactions = HashSet<String>()
        userTransactions.forEach {
            users.addAll(it.getUsersId())
            if (it.info is UserTransactionsIdCollector) {
                transactions.addAll(it.info.getTransactionsId())
            }
        }
        val usersMap = if (users.isNotEmpty()) userService.getUsersById(users) else emptyMap()
        val transactionsMap = if (transactions.isNotEmpty()) userTransactionService.getAll(transactions)
            else emptyMap<String, UserTransaction>()
        return userTransactions.map { asTo(it, usersMap, transactionsMap) }
    }

    fun asTo(userTransaction: UserTransaction): UserTransactionTo {
        val users = userTransaction.getUsersId()
        val usersMap = if (users.isNotEmpty()) userService.getUsersById(users) else emptyMap()
        val transactionsMap = when {
            userTransaction.info is UserTransactionsIdCollector
                    && userTransaction.info.getTransactionsId().isNotEmpty() -> userTransactionService.getAll(userTransaction.info.getTransactionsId())
            else -> emptyMap<String, UserTransaction>()
        }
        return asTo(userTransaction, usersMap, transactionsMap)
    }

    @Mappings(
            Mapping(target = "info", expression = "java(mapInfo(userTransaction, users, transactions))"),
            Mapping(target = "card", expression = "java(mapCard(userTransaction, users))")
    )
    abstract fun asTo(userTransaction: UserTransaction,
                      users: Map<Int, UserTo?>,
                      transactions: Map<String, UserTransaction>): UserTransactionTo

    @Mappings(
            Mapping(target = "info", expression = "java(null)"),
            Mapping(target = "card", expression = "java(mapCard(userTransaction, users))")
    )
    abstract fun asToIgnoreInfo(userTransaction: UserTransaction, users: Map<Int, UserTo?>): UserTransactionTo

    protected fun mapCard(userTransaction: UserTransaction, users: Map<Int, UserTo?>): CardToWithOwner {
        val user = users[userTransaction.card.userId]
        return cardMapper.asTo(userTransaction.card, user)
    }

    protected fun mapInfo(userTransaction: UserTransaction,
                          users: Map<Int, UserTo?>,
                          transactions: Map<String, UserTransaction>): UserTransactionInfoTo? =
            when (userTransaction.info) {
                is BuyCardUserTransactionInfo -> asTo(userTransaction.info)
                is AwardUserTransactionInfo -> asTo(userTransaction.info, users)
                is FineUserTransactionInfo -> asTo(userTransaction.info, users)
                is TranslateUserTransactionInfo -> asTo(userTransaction.info, users, transactions)
                is ReceiveUserTransactionInfo -> asTo(userTransaction.info, users, transactions)
                else -> null
            }

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
        val receiveTransaction = asToIgnoreInfo(transaction, users)
        return ReceiveUserTransactionInfoTo(receiveTransaction, receiveUserTransactionInfo.message)
    }
}