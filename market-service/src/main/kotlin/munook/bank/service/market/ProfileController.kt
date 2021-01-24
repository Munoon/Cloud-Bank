package munook.bank.service.market

import munoon.bank.common.AuthorizedUser
import munoon.bank.common.user.UserTo
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ProfileController {
    @GetMapping("/profile")
    fun profile(@AuthenticationPrincipal authorizedUser: AuthorizedUser): UserTo = authorizedUser.user
}