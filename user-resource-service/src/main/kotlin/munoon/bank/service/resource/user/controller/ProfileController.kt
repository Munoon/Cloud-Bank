package munoon.bank.service.resource.user.controller

import munoon.bank.common.AuthorizedUser
import munoon.bank.common.SecurityUtils.authUserId
import munoon.bank.common.user.UserTo
import munoon.bank.service.resource.user.client.TransactionClient
import munoon.bank.service.resource.user.user.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/profile")
class ProfileController(private val userService: UserService,
                        private val transactionClient: TransactionClient) {
    private val log = LoggerFactory.getLogger(ProfileController::class.java)

    @GetMapping
    fun getProfile(@AuthenticationPrincipal authorizedUser: AuthorizedUser): UserToWithCards {
        log.info("User ${authorizedUser.id} get his profile")
        val cards = transactionClient.getCardsByUserId(authorizedUser.id)
        return userService.getById(authorizedUser.id).asTo(cards)
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