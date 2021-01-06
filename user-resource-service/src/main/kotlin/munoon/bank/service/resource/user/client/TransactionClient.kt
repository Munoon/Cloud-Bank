package munoon.bank.service.resource.user.client

import munoon.bank.common.card.CardTo
import munoon.bank.common.transaction.to.PaySalaryTransactionDataTo
import munoon.bank.common.transaction.to.SalaryUserTransactionInfoTo
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@RequestMapping("/microservices")
@FeignClient(serviceId = "transactional-service")
interface TransactionClient {
    @PostMapping("/card/deactivate")
    fun deactivateCardsByUser(@RequestParam userId: Int)

    @GetMapping("/card/{userId}")
    fun getCardsByUserId(@PathVariable userId: Int): List<CardTo>

    @PostMapping("/transaction/payout/salary")
    fun payoutSalary(@RequestBody data: PaySalaryTransactionDataTo): SalaryUserTransactionInfoTo?
}