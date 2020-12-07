package munoon.bank.service.transactional.card

import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper
interface CardMapper {
    fun asTo(card: Card): CardTo

    companion object {
        val INSTANCE: CardMapper = Mappers.getMapper(CardMapper::class.java)
    }
}

fun Card.asTo() = CardMapper.INSTANCE.asTo(this)
fun List<Card>.asTo() = this.map { CardMapper.INSTANCE.asTo(it) }