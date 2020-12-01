package munoon.bank.service.auth.user

import munoon.bank.common.user.User
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper
interface LocalUserMapper {
    fun asUser(user: UserEntity): User

    companion object {
        val INSTANCE: LocalUserMapper = Mappers.getMapper(LocalUserMapper::class.java)
    }
}

fun UserEntity.asUser() = LocalUserMapper.INSTANCE.asUser(this)