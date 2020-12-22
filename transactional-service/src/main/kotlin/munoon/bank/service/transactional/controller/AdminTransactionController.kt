package munoon.bank.service.transactional.controller

import munoon.bank.common.SecurityUtils.authUserId
import munoon.bank.common.validation.pageable.size.PageSize
import munoon.bank.service.transactional.transaction.FineAwardDataTo
import munoon.bank.service.transactional.transaction.UserTransactionMapper
import munoon.bank.service.transactional.transaction.UserTransactionService
import munoon.bank.service.transactional.transaction.UserTransactionTo
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Validated
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

    @GetMapping("/{cardId}")
    fun getCardTransactions(@Valid @PageSize(min = 0, max = 20) @PageableDefault(page = 0, size = 20) pageable: Pageable,
                            @PathVariable cardId: String): Page<UserTransactionTo> {
        log.info("Admin {} request transactions of card '$cardId', page $pageable")
        val transactions = transactionService.getTransactions(cardId, userId = null, pageable)
        return transactionMapper.asTo(transactions)
    }
}