package munoon.bank.service.resource.user.controller

import munoon.bank.common.AuthorizedUser
import munoon.bank.common.SecurityUtils.authUserId
import munoon.bank.common.user.UserTo
import munoon.bank.service.resource.user.user.UpdatePasswordTo
import munoon.bank.service.resource.user.user.UpdateUsernameTo
import munoon.bank.service.resource.user.user.UserService
import munoon.bank.service.resource.user.user.asTo
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/profile")
class ProfileController(private val userService: UserService) {
    private val log = LoggerFactory.getLogger(ProfileController::class.java)

    @GetMapping
    fun getProfile(@AuthenticationPrincipal authorizedUser: AuthorizedUser): UserTo {
        log.info("User ${authorizedUser.id} get his profile")
        return userService.getById(authorizedUser.id).asTo()
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updatePassword(@Valid @RequestBody updatePasswordTo: UpdatePasswordTo) {
        log.info("User ${authUserId()} updated his password")
        userService.updateUser(authUserId(), updatePasswordTo)
    }

    @PutMapping
    fun updateUsername(@Valid @RequestBody updateUsernameTo: UpdateUsernameTo): UserTo {
        log.info("User ${authUserId()} updated his username to '${updateUsernameTo.newUsername}'")
        return userService.updateUser(authUserId(), updateUsernameTo).asTo()
    }
}