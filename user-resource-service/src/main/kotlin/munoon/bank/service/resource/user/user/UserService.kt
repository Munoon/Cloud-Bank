package munoon.bank.service.resource.user.user

import munoon.bank.common.user.User
import munoon.bank.service.resource.user.util.NotFoundException
import munoon.bank.service.resource.user.util.UserUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import javax.validation.ValidationException

@Service
class UserService(private val userRepository: UserRepository, private val passwordEncoder: PasswordEncoder) {
    fun getById(id: Int): User = getEntityById(id).asUser()

    fun getAll(pageable: Pageable): Page<User> = userRepository.findAll(pageable).map { it.asUser() }

    fun createUser(adminRegisterUserTo: AdminRegisterUserTo): User {
        val entity = adminRegisterUserTo.asEntity(passwordEncoder)
        return userRepository.save(entity).asUser()
    }

    fun updateUser(userId: Int, adminUpdateUserTo: AdminUpdateUserTo): User {
        val entity = getEntityById(userId)
        val updatedEntity = adminUpdateUserTo.asEntity(entity)
        return userRepository.save(updatedEntity).asUser()
    }

    fun updateUser(userId: Int, userTo: AdminUpdateUserPasswordTo): User {
        val entity = getEntityById(userId)
        val newPassword = passwordEncoder.encode(userTo.password)
        val updatedEntity = entity.copy(password = newPassword)
        return userRepository.save(updatedEntity).asUser()
    }

    fun updateUser(userId: Int, updatePasswordTo: UpdatePasswordTo) {
        val entity = getEntityById(userId)
        UserUtils.validatePassword(updatePasswordTo.oldPassword, entity.password, passwordEncoder, fieldName = "oldPassword")
        val updatedEntity = entity.copy(password = passwordEncoder.encode(updatePasswordTo.newPassword))
        userRepository.save(updatedEntity)
    }

    fun updateUser(userId: Int, updateUsernameTo: UpdateUsernameTo): User {
        val entity = getEntityById(userId)
        UserUtils.validatePassword(updateUsernameTo.password, entity.password, passwordEncoder)
        val updatedEntity = entity.copy(username = updateUsernameTo.newUsername)
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