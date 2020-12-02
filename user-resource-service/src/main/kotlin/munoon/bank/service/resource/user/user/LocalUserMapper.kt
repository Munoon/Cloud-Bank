package munoon.bank.service.resource.user.user

import munoon.bank.common.user.User
import munoon.bank.common.user.UserMapper
import munoon.bank.common.user.UserTo
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper
interface LocalUserMapper {
    fun asUser(user: UserEntity): User

    companion object {
        val INSTANCE: LocalUserMapper = Mappers.getMapper(LocalUserMapper::class.java)
    }
}

fun UserEntity.asUser(): User = LocalUserMapper.INSTANCE.asUser(this)
fun User.asTo(): UserTo = UserMapper.INSTANCE.asTo(this)