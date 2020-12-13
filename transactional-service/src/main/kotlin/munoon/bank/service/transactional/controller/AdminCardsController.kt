package munoon.bank.service.transactional.controller

import munoon.bank.common.SecurityUtils.authUserId
import munoon.bank.service.transactional.card.*
import munoon.bank.service.transactional.util.CardUtils
import org.hibernate.validator.constraints.Length
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Validated
@RestController
@RequestMapping("/admin/cards")
@PreAuthorize("hasRole('ROLE_ADMIN')")
class AdminCardsController(private val cardService: CardService) {
    private val log = LoggerFactory.getLogger(AdminCardsController::class.java)

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    fun getCardsByUser(@PathVariable userId: Int): List<CardTo> {
        log.info("User ${authUserId()} request cards of user $userId")
        return cardService.getCardsByUserId(userId).asTo()
    }

    @GetMapping("/number/{cardNumber}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    fun getCardByNumber(@Valid @Length(min = 12, max = 12) @PathVariable cardNumber: String): CardTo {
        log.info("User ${authUserId()} request card by number '$cardNumber'")
        return cardService.getCardByNumber(cardNumber).asTo()
    }

    @GetMapping("/{userId}/{cardId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    fun getCardById(@PathVariable userId: Int, @PathVariable cardId: String): CardTo {
        log.info("User ${authUserId()} request cards of user $userId with id '$cardId'")
        val card = cardService.getCardById(cardId)
        CardUtils.checkCardOwner(userId, card)
        return card.asTo()
    }

    @PutMapping("/{userId}/{cardId}")
    fun updateCard(@PathVariable userId: Int, @PathVariable cardId: String,
                   @Valid @RequestBody adminUpdateCardTo: AdminUpdateCardTo): CardTo {
        log.info("Admin ${authUserId()} updated card '$cardId' of user $userId: $adminUpdateCardTo")
        return cardService.updateCard(userId, cardId, adminUpdateCardTo).asTo()
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{userId}/{cardId}/pinCode")
    fun updateCardPinCode(@PathVariable userId: Int, @PathVariable cardId: String,
                          @Valid @RequestBody adminUpdateCardPinCode: AdminUpdateCardPinCode) {
        log.info("Admin ${authUserId()} updated pinCode of card '$cardId' of user $userId")
        cardService.updateCardPinCode(userId, cardId, adminUpdateCardPinCode.pinCode)
    }

    @PostMapping
    fun createCard(@Valid @RequestBody adminCreateCardTo: AdminCreateCardTo): CardTo {
        log.info("Admin ${authUserId()} create card: $adminCreateCardTo")
        return cardService.createCard(adminCreateCardTo).asTo()
    }
}