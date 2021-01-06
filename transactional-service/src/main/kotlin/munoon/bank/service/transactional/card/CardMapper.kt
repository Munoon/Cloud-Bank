package munoon.bank.service.transactional.card

import munoon.bank.common.card.CardTo
import munoon.bank.common.user.UserTo
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

    @Autowired
    protected lateinit var passwordEncoder: PasswordEncoder

    abstract fun asTo(card: Card): CardTo

    fun asSafeTo(card: Card): SafeCardToWithOwner {
        val user = userService.getUserOrNull(card.userId)
        return asSafeTo(card, user)
    }

    fun asToWithUser(card: Card): CardToWithOwner {
        val user = userService.getUserOrNull(card.userId)
        return asTo(card, user)
    }

    @Mappings(
            Mapping(target = "owner", source = "userTo"),
            Mapping(target = "id", source = "card.id"),
            Mapping(target = "registered", source = "card.registered")
    )
    abstract fun asTo(card: Card, userTo: UserTo?): CardToWithOwner

    @Mappings(
            Mapping(target = "owner", source = "userTo"),
            Mapping(target = "id", source = "card.id")
    )
    abstract fun asSafeTo(card: Card, userTo: UserTo?): SafeCardToWithOwner

    abstract fun updateCard(adminUpdateCardTo: AdminUpdateCardTo, @MappingTarget card: Card): Card

    @Mappings(
            Mapping(target = "id", expression = "java(null)"),
            Mapping(target = "number", expression = "java(null)"),
            Mapping(target = "pinCode", expression = "java(passwordEncoder.encode(buyCardTo.getPinCode()))"),
            Mapping(target = "balance", constant = "0.0"),
            Mapping(target = "active", constant = "true"),
            Mapping(target = "registered", expression = "java(java.time.LocalDateTime.now())"),
            Mapping(target = "primary", source = "primary")
    )
    abstract fun asCard(buyCardTo: BuyCardTo, userId: Int, primary: Boolean): Card

    @Mappings(
            Mapping(target = "id", expression = "java(null)"),
            Mapping(target = "balance", constant = "0.0"),
            Mapping(target = "primary", source = "primary"),
            Mapping(target = "registered", expression = "java(java.time.LocalDateTime.now())"),
            Mapping(target = "pinCode", expression = "java(passwordEncoder.encode(source.getPinCode()))"),
    )
    abstract fun asCard(source: AdminCreateCardTo, primary: Boolean): Card
}