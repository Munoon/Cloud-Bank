package munoon.bank.service.resource.user.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
interface UserRepository : JpaRepository<UserEntity, Int> {
    @Modifying
    @Transactional
    @Query("DELETE FROM UserEntity WHERE id = ?1")
    fun deleteUserById(id: Int): Int
}