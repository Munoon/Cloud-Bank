package munoon.bank.service.resource.user.util

import org.springframework.security.crypto.password.PasswordEncoder

class TestPasswordEncoder : PasswordEncoder {
    override fun encode(password: CharSequence): String = PREFIX + password

    override fun matches(type: CharSequence, actual: String): Boolean {
        val typeStr = type.toString()
        if (!typeStr.startsWith(PREFIX)) {
            return false
        }

        return type.substring(PREFIX.length) == actual
    }

    companion object {
        private const val PREFIX = "{test}"
    }
}