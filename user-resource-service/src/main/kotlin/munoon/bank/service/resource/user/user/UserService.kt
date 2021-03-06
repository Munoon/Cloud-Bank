package munoon.bank.service.resource.user.user

import munoon.bank.common.user.User
import munoon.bank.common.util.exception.NotFoundException
import munoon.bank.service.resource.user.client.TransactionClient
import munoon.bank.service.resource.user.util.UserUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository,
                  private val passwordEncoder: PasswordEncoder,
                  private val transactionClient: TransactionClient) {
    fun getById(id: Int): User = getEntityById(id).asUser()

    fun getAll(pageable: Pageable, clazz: String): Page<User> = userRepository.findAllByClazz(pageable, clazz)
            .map { it.asUser() }

    fun findUser(pageable: Pageable, query: String) = userRepository.findByQuery(query.toLowerCase(), pageable)
            .map { it.asUser() }

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
        transactionClient.deactivateCardsByUser(id)
    }

    fun getUsersByIds(ids: List<Int>) = userRepository.findAllById(ids)
            .map { it.asUser() }

    private fun getEntityById(id: Int) = userRepository.findById(id)
            .orElseThrow { NotFoundException("User with id $id is not found!") }
}