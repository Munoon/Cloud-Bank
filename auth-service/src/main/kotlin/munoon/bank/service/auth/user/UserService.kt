package munoon.bank.service.auth.user

import munoon.bank.common.AuthorizedUser
import munoon.bank.common.user.User
import munoon.bank.service.auth.util.NotFoundException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails = userRepository.getByUsername(username)
            .map { AuthorizedUser(it.asUser()) }
            .orElseThrow { UsernameNotFoundException("User with username '$username' is not found!") }

    fun getById(id: Int): User = userRepository.findById(id)
            .map { it.asUser() }
            .orElseThrow { NotFoundException("User with id $id is not found!") }
}