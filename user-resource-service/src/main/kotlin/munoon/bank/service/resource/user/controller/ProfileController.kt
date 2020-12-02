package munoon.bank.service.resource.user.controller

import munoon.bank.common.AuthorizedUser
import munoon.bank.common.user.UserTo
import munoon.bank.service.resource.user.user.UserService
import munoon.bank.service.resource.user.user.asTo
import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/profile")
class ProfileController(private val userService: UserService) {
    private val log = LoggerFactory.getLogger(ProfileController::class.java)

    @GetMapping
    fun getProfile(@AuthenticationPrincipal authorizedUser: AuthorizedUser): UserTo {
        log.info("User ${authorizedUser.id} get his profile")
        return userService.getById(authorizedUser.id).asTo()
    }
}