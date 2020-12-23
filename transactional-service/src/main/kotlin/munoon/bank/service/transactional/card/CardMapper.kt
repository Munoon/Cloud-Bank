package munoon.bank.service.transactional.card

import munoon.bank.common.card.CardTo
import munoon.bank.common.user.UserTo
import munoon.bank.common.util.exception.NotFoundException
import munoon.bank.service.transactional.user.UserService
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder

@Mapper(componentModel = "spring")
abstract class CardMapper {
    @Autowired
    private lateinit var userService: UserService

    abstract fun asTo(card: Card): CardTo

    fun asToWithUser(card: Card): CardToWithOwner {
        val user = try {
            userService.getUserById(card.userId)
        } catch (e: NotFoundException) {
            null
        }
        return asTo(card, user)
    }

    @Mappings(
            Mapping(target = "owner", source = "userTo"),
            Mapping(target = "id", source = "card.id"),
            Mapping(target = "registered", source = "card.registered")
    )
    abstract fun asTo(card: Card, userTo: UserTo?): CardToWithOwner

    abstract fun updateCard(adminUpdateCardTo: AdminUpdateCardTo, @MappingTarget card: Card): Card

    @Mappings(
            Mapping(target = "id", expression = "java(null)"),
            Mapping(target = "number", expression = "java(null)"),
            Mapping(target = "pinCode", source = "pinCode"),
            Mapping(target = "balance", constant = "0.0"),
            Mapping(target = "active", constant = "true"),
            Mapping(target = "registered", expression = "java(java.time.LocalDateTime.now())")
    )
    abstract fun asCard(buyCardTo: BuyCardTo, userId: Int, pinCode: String): Card

    @Mappings(
            Mapping(target = "id", expression = "java(null)"),
            Mapping(target = "balance", constant = "0.0"),
            Mapping(target = "registered", expression = "java(java.time.LocalDateTime.now())"),
            Mapping(target = "pinCode", expression = "java(passwordEncoder.encode(source.getPinCode()))"),
    )
    abstract fun asCard(source: AdminCreateCardTo, passwordEncoder: PasswordEncoder): Card
}