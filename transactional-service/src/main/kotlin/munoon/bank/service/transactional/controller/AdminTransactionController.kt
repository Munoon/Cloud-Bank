package munoon.bank.service.transactional.controller

import munoon.bank.common.SecurityUtils.authUserId
import munoon.bank.service.transactional.transaction.*
import munoon.bank.service.transactional.transaction.operator.CancelTransactionFlag
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
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
        val data = FineAwardTransactionInfoData(authUserId(), fineAwardData)
        val transaction = transactionService.makeTransaction(data)
        return transactionMapper.asToWithUnSafeInfo(transaction!!)
    }

    @GetMapping("/card/{cardId}")
    fun getCardTransactions(@PageableDefault(page = 0, size = 20) pageable: Pageable,
                            @PathVariable cardId: String): Page<UserTransactionTo> {
        log.info("Admin ${authUserId()} request transactions of card '$cardId', page $pageable")
        val transactions = transactionService.getTransactions(cardId, userId = null, pageable)
        return transactionMapper.asToWithUnSafeInfo(transactions)
    }

    @PostMapping("/{transactionId}/cancel")
    fun cancelTransaction(@PathVariable transactionId: String,
                          @RequestParam(defaultValue = "") flags: Set<CancelTransactionFlag>): UserTransactionTo {
        log.info("Admin ${authUserId()} canceled transaction '$transactionId'")
        val transaction = transactionService.cancelTransaction(transactionId, flags)
        return transactionMapper.asToWithUnSafeInfo(transaction)
    }
}