package munoon.bank.service.resource.user.user

import munoon.bank.common.user.User
import munoon.bank.service.resource.user.util.NotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository, private val passwordEncoder: PasswordEncoder) {
    fun getById(id: Int): User = getEntityById(id).asUser()

    fun getAll(pageable: Pageable): Page<User> = userRepository.findAll(pageable).map { it.asUser() }

    fun createUser(adminRegisterUserTo: AdminRegisterUserTo): User {
        val entity = adminRegisterUserTo.asEntity(passwordEncoder)
        return userRepository.save(entity).asUser()
    }

    fun updateUser(id: Int, adminUpdateUserTo: AdminUpdateUserTo): User {
        val entity = getEntityById(id)
        val updatedEntity = adminUpdateUserTo.asEntity(entity)
        return userRepository.save(updatedEntity).asUser()
    }

    fun updateUser(id: Int, userTo: AdminUpdateUserPasswordTo): User {
        val entity = getEntityById(id)
        val newPassword = passwordEncoder.encode(userTo.password)
        val updatedEntity = entity.copy(password = newPassword)
        return userRepository.save(updatedEntity).asUser()
    }

    fun removeUser(id: Int) {
        val modified = userRepository.deleteUserById(id)
        if (modified == 0) {
            throw NotFoundException("User with id $id is not found")
        }
    }

    private fun getEntityById(id: Int) = userRepository.findById(id)
            .orElseThrow { NotFoundException("User with id $id is not found!") }
}