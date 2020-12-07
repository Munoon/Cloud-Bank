package munoon.bank.service.transactional.transaction

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page

@Mapper
abstract class UserTransactionMapper {
    @Mapping(target = "info", source = "userTransaction")
    abstract fun asTo(userTransaction: UserTransaction): UserTransactionTo

    fun mapInfo(userTransaction: UserTransaction): UserTransactionInfoTo? = when (userTransaction.info) {
        is BuyCardUserTransactionInfo -> asTo(userTransaction.info)
        else -> null
    }

    abstract fun asTo(buyCardUserTransactionInfo: BuyCardUserTransactionInfo): BuyCardUserTransactionInfoTo

    companion object {
        val INSTANCE = Mappers.getMapper(UserTransactionMapper::class.java)
    }
}

fun UserTransaction.asTo() = UserTransactionMapper.INSTANCE.asTo(this)
fun Page<UserTransaction>.asTo() = this.map { it.asTo() }