package munoon.bank.service.resource.user.controller

import munoon.bank.common.SecurityUtils.authUserId
import munoon.bank.common.user.UserTo
import munoon.bank.service.resource.user.user.*
import munoon.bank.service.resource.user.util.validator.PageableSize
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
class AdminController(private val userService: UserService) {
    private val log = LoggerFactory.getLogger(AdminController::class.java)

    @GetMapping
    fun getUsersList(@Valid @PageableSize(max = 20) @PageableDefault(size = 10, page = 0) pageable: Pageable): Page<UserTo> {
        log.info("Admin ${authUserId()} get users list: $pageable")
        return userService.getAll(pageable).map { it.asTo() }
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Int): UserTo {
        log.info("Admin ${authUserId()} get user with id $id")
        return userService.getById(id).asTo()
    }

    @PostMapping
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