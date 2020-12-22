package munoon.bank.service.transactional.controller

import munoon.bank.common.SecurityUtils.authUserId
import munoon.bank.service.transactional.card.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Validated
@RestController
@RequestMapping("/cards")
class UserCardsController(private val cardService: CardService,
                          private val cardMapper: CardMapper) {
    private val log = LoggerFactory.getLogger(UserCardsController::class.java)

    @PostMapping("/buy")
    fun buyCard(@RequestBody @Valid buyCardTo: BuyCardTo): CardTo {
        log.info("User ${authUserId()} buy card: $buyCardTo")
        val card = cardService.buyCard(authUserId(), buyCardTo)
        return cardMapper.asTo(card)
    }

    @GetMapping
    fun getCards(): List<CardTo> {
        log.info("User ${authUserId()} get his cards list")
        return cardService.getCardsByUserId(authUserId()).map { cardMapper.asTo(it) }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{cardId}/pinCode")
    fun updateCardPinCode(@PathVariable cardId: String, @Valid @RequestBody updatePinCode: UserUpdateCardPinCode) {
        log.info("User ${authUserId()} update pinCode of card '$cardId'")
        cardService.updateCardPinCode(authUserId(), cardId, updatePinCode)
    }
}