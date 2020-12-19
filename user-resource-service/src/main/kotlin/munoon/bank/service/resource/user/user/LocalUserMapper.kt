package munoon.bank.service.resource.user.user

import munoon.bank.common.user.User
import munoon.bank.common.user.UserMapper
import munoon.bank.common.user.UserTo
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers
import org.springframework.security.crypto.password.PasswordEncoder

@Mapper
interface LocalUserMapper {
    fun asUser(user: UserEntity): User

    @Mappings(
            Mapping(target = "registered", expression = "java(java.time.LocalDateTime.now())"),
            Mapping(target = "password", expression = "java(passwordEncoder.encode(user.getPassword()))"),
            Mapping(target = "id", expression = "java(null)")
    )
    fun asUserEntity(user: AdminRegisterUserTo, passwordEncoder: PasswordEncoder): UserEntity

    fun asUserEntity(adminUpdateUserTo: AdminUpdateUserTo, @MappingTarget user: UserEntity): UserEntity

    companion object {
        val INSTANCE: LocalUserMapper = Mappers.getMapper(LocalUserMapper::class.java)
    }
}

fun UserEntity.asUser(): User = LocalUserMapper.INSTANCE.asUser(this)
fun User.asTo(): UserTo = UserMapper.INSTANCE.asTo(this)
fun AdminRegisterUserTo.asEntity(passwordEncoder: PasswordEncoder) = LocalUserMapper.INSTANCE.asUserEntity(this, passwordEncoder)
fun AdminUpdateUserTo.asEntity(entity: UserEntity) = LocalUserMapper.INSTANCE.asUserEntity(this, entity)