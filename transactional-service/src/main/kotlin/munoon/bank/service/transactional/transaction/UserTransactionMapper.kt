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
    protected lateinit var cardMapper: CardMapper

    fun asTo(userTransactions: Page<UserTransaction>): Page<UserTransactionTo> {
        val users = HashSet<Int>()
        userTransactions.forEach { users.addAll(injectUsersIds(it)) }
        val usersMap = if (users.isNotEmpty()) userService.getUsersById(users) else emptyMap()
        return userTransactions.map { asTo(it, usersMap) }
    }

    fun asTo(userTransaction: UserTransaction): UserTransactionTo {
        val users = injectUsersIds(userTransaction)
        val usersMap = if (users.isNotEmpty()) userService.getUsersById(users) else emptyMap()
        return asTo(userTransaction, usersMap)
    }

    @Mappings(
            Mapping(target = "info", expression = "java(mapInfo(userTransaction, users))"),
            Mapping(target = "card", expression = "java(mapCard(userTransaction, users))")
    )
    abstract fun asTo(userTransaction: UserTransaction, users: Map<Int, UserTo?>): UserTransactionTo

    protected fun mapCard(userTransaction: UserTransaction, users: Map<Int, UserTo?>): CardToWithOwner {
        val user = users[userTransaction.card.userId]
        return cardMapper.asTo(userTransaction.card, user)
    }

    protected fun mapInfo(userTransaction: UserTransaction, users: Map<Int, UserTo?>): UserTransactionInfoTo? = when (userTransaction.info) {
        is BuyCardUserTransactionInfo -> asTo(userTransaction.info)
        is AwardUserTransactionInfo -> asTo(userTransaction.info, users)
        is FineUserTransactionInfo -> asTo(userTransaction.info, users)
        else -> null
    }

    protected abstract fun asTo(buyCardUserTransactionInfo: BuyCardUserTransactionInfo): BuyCardUserTransactionInfoTo

    @Mapping(target = "user", expression = "java(users.get(awardUserTransactionInfo.getUserId()))")
    protected abstract fun asTo(awardUserTransactionInfo: AwardUserTransactionInfo, users: Map<Int, UserTo?>): AwardUserTransactionInfoTo

    @Mapping(target = "user", expression = "java(users.get(fineUserTransactionInfo.getUserId()))")
    protected abstract fun asTo(fineUserTransactionInfo: FineUserTransactionInfo, users: Map<Int, UserTo?>): FineUserTransactionInfoTo

    private fun injectUsersIds(userTransaction: UserTransaction): Set<Int> {
        val users = mutableSetOf(userTransaction.card.userId)
        if (userTransaction.info is UsersCollectorTransactionInfo) {
            users.addAll(userTransaction.info.getUsersId())
        }
        return users
    }
}