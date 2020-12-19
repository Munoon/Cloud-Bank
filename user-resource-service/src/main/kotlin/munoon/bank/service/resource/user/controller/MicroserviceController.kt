package munoon.bank.service.resource.user.controller

import munoon.bank.common.user.UserTo
import munoon.bank.service.resource.user.user.UserService
import munoon.bank.service.resource.user.user.asTo
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/microservices/users")
class MicroserviceController(private val userService: UserService) {
    private val log = LoggerFactory.getLogger(MicroserviceController::class.java)

    @GetMapping
    fun getUsersById(@RequestParam("ids") ids: List<Int>): List<UserTo> {
        log.info("Microservice get users with ids $ids")
        return userService.getUsersByIds(ids).map { it.asTo() }
    }
}