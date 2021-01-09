package munoon.bank.service.transactional.controller

import munoon.bank.common.transaction.to.PaySalaryTransactionDataTo
import munoon.bank.common.transaction.to.SalaryUserTransactionInfoTo
import munoon.bank.common.util.MicroserviceUtils.getMicroserviceName
import munoon.bank.service.transactional.transaction.*
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/microservices/transaction")
class MicroserviceTransactionController(private val transactionService: UserTransactionService,
                                        private val transactionMapper: UserTransactionMapper) {
    private val log = LoggerFactory.getLogger(MicroserviceTransactionController::class.java)

    @PostMapping("/payout/salary")
    fun paySalary(@RequestBody paySalaryTransactionDataTo: PaySalaryTransactionDataTo): SalaryUserTransactionInfoTo? {
        log.info("Microservice ${getMicroserviceName()} pay salary: $paySalaryTransactionDataTo")
        val data = SalaryTransactionInfoData(paySalaryTransactionDataTo.userId, paySalaryTransactionDataTo.count)
        val transaction = transactionService.makeTransaction(data) ?: return null
        return transactionMapper.asPaySalaryTo(transaction)
    }

    @PostMapping("/payout/card/service")
    fun payCardService(@RequestBody cardId: String): UserTransactionTo {
        log.info("Microservice ${getMicroserviceName()} pay card service to card '$cardId'")
        val data = CardServiceTransactionInfoData(cardId)
        val transaction = transactionService.makeTransaction(data)
        return transactionMapper.asToIgnoreInfo(transaction!!, emptyMap())
    }
}