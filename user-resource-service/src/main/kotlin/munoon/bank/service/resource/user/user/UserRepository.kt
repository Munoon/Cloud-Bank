package munoon.bank.service.resource.user.user

import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<UserEntity, Int>