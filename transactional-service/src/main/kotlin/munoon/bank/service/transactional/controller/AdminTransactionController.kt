package munoon.bank.service.transactional.controller

import munoon.bank.common.SecurityUtils.authUserId
import munoon.bank.service.transactional.transaction.FineAwardDataTo
import munoon.bank.service.transactional.transaction.UserTransactionMapper
import munoon.bank.service.transactional.transaction.UserTransactionService
import munoon.bank.service.transactional.transaction.UserTransactionTo
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/admin/transaction")
class AdminTransactionController(private val transactionService: UserTransactionService,
                                 private val transactionMapper: UserTransactionMapper) {
    private val log = LoggerFactory.getLogger(AdminTransactionController::class.java)

    @PostMapping("/fine-award")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    fun makeFineOrAward(@Valid @RequestBody fineAwardData: FineAwardDataTo): UserTransactionTo {
        log.info("Admin ${authUserId()} make fine or award: $fineAwardData")
        val transaction = transactionService.fineAwardTransaction(authUserId(), fineAwardData)
        return transactionMapper.asTo(transaction)
    }
}