package munoon.bank.service.transactional.user

import munoon.bank.common.user.UserTo
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(serviceId = "user-resource-service")
interface UserClient {
    @GetMapping("/profile", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) token: String): UserTo
}