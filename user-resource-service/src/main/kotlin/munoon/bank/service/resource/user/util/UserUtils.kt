package munoon.bank.service.resource.user.util

import munoon.bank.common.util.exception.FieldValidationException
import org.springframework.security.crypto.password.PasswordEncoder

object UserUtils {
    fun validatePassword(password: CharSequence,
                         actual: String,
                         passwordEncoder: PasswordEncoder,
                         message: String = "Неверный пароль",
                         fieldName: String = "password") {
        if (!passwordEncoder.matches(password, actual)) {
            throw FieldValidationException(message, fieldName)
        }
    }
}