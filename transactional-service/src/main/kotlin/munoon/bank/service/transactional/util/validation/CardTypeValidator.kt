package munoon.bank.service.transactional.util.validation

import munoon.bank.service.transactional.card.CardsProperties
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [CardTypeValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
annotation class CardType(
    val message: String = "Такого типа карты не существует!",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class CardTypeValidator(private val cardProperties: CardsProperties) : ConstraintValidator<CardType, String> {
    override fun isValid(value: String, context: ConstraintValidatorContext?) = cardProperties.cards.stream().anyMatch {
        it.codeName == value
    }
}