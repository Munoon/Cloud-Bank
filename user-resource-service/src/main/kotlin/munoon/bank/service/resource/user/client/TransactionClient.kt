package munoon.bank.service.resource.user.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(serviceId = "transactional-service")
interface TransactionClient {
    @PostMapping("/microservices/card/deactivate")
    fun deactivateCardsByUser(@RequestParam userId: Int)
}