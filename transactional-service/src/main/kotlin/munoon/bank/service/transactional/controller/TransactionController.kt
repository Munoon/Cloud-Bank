package munoon.bank.service.transactional.controller

import munoon.bank.common.SecurityUtils.authUserId
import munoon.bank.common.validation.pageable.size.PageSize
import munoon.bank.service.transactional.transaction.UserTransactionService
import munoon.bank.service.transactional.transaction.UserTransactionTo
import munoon.bank.service.transactional.transaction.asTo
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Validated
@RestController
@RequestMapping("/transaction/{cardId}")
class TransactionController(private val transactionService: UserTransactionService) {
    private val log = LoggerFactory.getLogger(TransactionController::class.java)

    @GetMapping
    fun getTransactionsList(@Valid @PageSize(min = 0, max = 20) @PageableDefault(page = 0, size = 20) pageable: Pageable,
                            @PathVariable cardId: String): Page<UserTransactionTo> {
        log.info("User ${authUserId()} get transactions list of card $cardId")
        return transactionService.getTransactions(cardId, authUserId(), pageable).asTo()
    }
}