package munoon.bank.service.transactional.card

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers
import org.springframework.security.crypto.password.PasswordEncoder

@Mapper
interface CardMapper {
    fun asTo(card: Card): CardTo
    fun updateCard(adminUpdateCardTo: AdminUpdateCardTo, @MappingTarget card: Card): Card

    @Mappings(
            Mapping(target = "id", expression = "java(null)"),
            Mapping(target = "number", expression = "java(null)"),
            Mapping(target = "pinCode", source = "pinCode"),
            Mapping(target = "balance", constant = "0.0"),
            Mapping(target = "registered", expression = "java(java.time.LocalDateTime.now())")
    )
    fun asCard(buyCardTo: BuyCardTo, userId: Int, pinCode: String): Card

    @Mappings(
            Mapping(target = "id", expression = "java(null)"),
            Mapping(target = "balance", constant = "0.0"),
            Mapping(target = "registered", expression = "java(java.time.LocalDateTime.now())"),
            Mapping(target = "pinCode", expression = "java(passwordEncoder.encode(source.getPinCode()))"),
    )
    fun asCard(source: AdminCreateCardTo, passwordEncoder: PasswordEncoder): Card

    companion object {
        val INSTANCE: CardMapper = Mappers.getMapper(CardMapper::class.java)
    }
}

fun Card.asTo() = CardMapper.INSTANCE.asTo(this)
fun List<Card>.asTo() = this.map { CardMapper.INSTANCE.asTo(it) }
fun BuyCardTo.asCard(userId: Int, pinCode: String) = CardMapper.INSTANCE.asCard(this, userId, pinCode)