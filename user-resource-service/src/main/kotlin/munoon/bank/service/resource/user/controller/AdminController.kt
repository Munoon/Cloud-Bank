package munoon.bank.service.resource.user.controller

import munoon.bank.common.SecurityUtils.authUserId
import munoon.bank.common.user.UserTo
import munoon.bank.common.validation.pageable.size.PageSize
import munoon.bank.service.resource.user.client.TransactionClient
import munoon.bank.service.resource.user.config.ClassesProperties
import munoon.bank.service.resource.user.user.*
import munoon.bank.service.resource.user.util.validator.ValidClass
import org.hibernate.validator.constraints.Length
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Validated
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
class AdminController(private val userService: UserService,
                      private val transactionClient: TransactionClient,
                      private val classesProperties: ClassesProperties) {
    private val log = LoggerFactory.getLogger(AdminController::class.java)

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    fun getUsersList(@Valid @PageSize(max = 20) @PageableDefault(size = 10, page = 0) pageable: Pageable,
                     @Valid @ValidClass @RequestParam("class") clazz: String): Page<UserTo> {
        log.info("User ${authUserId()} get users list by class $clazz: $pageable")
        return userService.getAll(pageable, clazz).map { it.asTo() }
    }

    @GetMapping("/classes")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    fun getClassesList(): List<String> {
        log.info("User ${authUserId()} get classes list")
        return classesProperties.classes
    }

    @GetMapping("/find")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    fun findUser(@Valid @PageSize(max = 20) @PageableDefault(size = 10, page = 0) pageable: Pageable,
                 @Valid @Length(min = 3, max = 30) @RequestParam query: String): Page<UserTo> {
        log.info("User ${authUserId()} find user by query '$query' (page: $pageable)")
        return userService.findUser(pageable, query).map { it.asTo() }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    fun getUser(@PathVariable id: Int): UserToWithCards {
        log.info("User ${authUserId()} get user with id $id")
        val user = userService.getById(id)
        val cards = transactionClient.getCardsByUserId(id)
        return user.asTo(cards)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@Valid @RequestBody adminRegisterUserTo: AdminRegisterUserTo): UserTo {
        log.info("Admin ${authUserId()} create user: $adminRegisterUserTo")
        return userService.createUser(adminRegisterUserTo).asTo()
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Int, @Valid @RequestBody userTo: AdminUpdateUserTo): UserTo {
        log.info("Admin ${authUserId()} updated user $id: $userTo")
        return userService.updateUser(id, userTo).asTo()
    }

    @PutMapping("/{id}/password")
    fun updateUserPassword(@PathVariable id: Int, @Valid @RequestBody userTo: AdminUpdateUserPasswordTo): UserTo {
        log.info("Admin ${authUserId()} updated user $id password")
        return userService.updateUser(id, userTo).asTo()
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(@PathVariable id: Int) {
        log.info("Admin ${authUserId()} deleted user $id")
        userService.removeUser(id)
    }
}