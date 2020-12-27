package munoon.bank.service.transactional.controller

import munoon.bank.common.SecurityUtils.authUserId
import munoon.bank.common.validation.pageable.size.PageSize
import munoon.bank.service.transactional.transaction.*
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Validated
@RestController
@RequestMapping("/transaction")
class UserTransactionController(private val transactionService: UserTransactionService,
                                private val transactionMapper: UserTransactionMapper) {
    private val log = LoggerFactory.getLogger(UserTransactionController::class.java)

    @GetMapping("/{cardId}")
    fun getTransactionsList(@Valid @PageSize(min = 0, max = 20) @PageableDefault(page = 0, size = 20) pageable: Pageable,
                            @PathVariable cardId: String): Page<UserTransactionTo> {
        log.info("User ${authUserId()} get transactions list of card $cardId")
        val transactions = transactionService.getTransactions(cardId, authUserId(), pageable)
        return transactionMapper.asToWithSafeInfo(transactions)
    }

    @PostMapping("/translate")
    fun translateMoney(@RequestBody @Valid translateMoneyDataTo: TranslateMoneyDataTo): UserTransactionTo {
        log.info("User ${authUserId()} translate money: $translateMoneyDataTo")
        val data = TranslateMoneyTransactionInfoData(authUserId(), translateMoneyDataTo)
        val transaction = transactionService.makeTransaction(data)
        return transactionMapper.asToWithSafeInfo(transaction)
    }
}