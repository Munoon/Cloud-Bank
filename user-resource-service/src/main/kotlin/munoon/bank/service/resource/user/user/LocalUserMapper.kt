package munoon.bank.service.resource.user.user

import munoon.bank.common.card.CardTo
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

    @Mapping(target = "cards", source = "cards")
    fun asTo(user: User, cards: List<CardTo>): UserToWithCards

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
fun User.asTo(cards: List<CardTo>): UserToWithCards = LocalUserMapper.INSTANCE.asTo(this, cards)
fun AdminRegisterUserTo.asEntity(passwordEncoder: PasswordEncoder) = LocalUserMapper.INSTANCE.asUserEntity(this, passwordEncoder)
fun AdminUpdateUserTo.asEntity(entity: UserEntity) = LocalUserMapper.INSTANCE.asUserEntity(this, entity)