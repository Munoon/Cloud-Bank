package munoon.bank.service.resource.user.user

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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

    fun findAllByClazz(pageable: Pageable, clazz: String): Page<UserEntity>

    @Query("FROM UserEntity u WHERE LOWER(u.name) LIKE %?1% " +
            "OR LOWER(u.surname) LIKE %?1% " +
            "OR LOWER(CONCAT(u.name, ' ', u.surname)) LIKE %?1% " +
            "OR LOWER(CONCAT(u.surname, ' ', u.name)) LIKE %?1%")
    fun findByQuery(query: String, pageable: Pageable): Page<UserEntity>
}