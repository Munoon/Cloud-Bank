package munoon.bank.service.auth.user

import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserRepository : CrudRepository<UserEntity, Int> {
    fun getByUsername(username: String): Optional<UserEntity>
}