package munoon.bank.service.transactional.user

import munoon.bank.common.user.UserTo
import munoon.bank.service.transactional.config.DefaultFeignConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(serviceId = "user-resource-service", configuration = [DefaultFeignConfig::class])
interface UserClient {
    @GetMapping("/profile", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) token: String): UserTo

    @GetMapping("/microservices/users", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getUsersById(@RequestParam("ids") ids: List<Int>): List<UserTo>
}