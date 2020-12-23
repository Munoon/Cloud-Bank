package munoon.bank.service.resource.user.client

import munoon.bank.common.card.CardTo
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@RequestMapping("/microservices")
@FeignClient(serviceId = "transactional-service")
interface TransactionClient {
    @PostMapping("/card/deactivate")
    fun deactivateCardsByUser(@RequestParam userId: Int)

    @GetMapping("/card/{userId}")
    fun getCardsByUserId(@PathVariable userId: Int): List<CardTo>
}