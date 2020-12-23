package munoon.bank.service.transactional.controller

import munoon.bank.common.card.CardTo
import munoon.bank.common.util.MicroserviceUtils.getMicroserviceName
import munoon.bank.service.transactional.card.CardMapper
import munoon.bank.service.transactional.card.CardService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/microservices/card")
class MicroserviceCardController(private val cardService: CardService,
                                 private val cardMapper: CardMapper) {
    private val log = LoggerFactory.getLogger(MicroserviceCardController::class.java)

    @PostMapping("/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deactivateByUser(@RequestParam userId: Int) {
        log.info("Microservice ${getMicroserviceName()} deactivate cards with owner $userId")
        cardService.deactivateAllByOwner(userId)
    }

    @GetMapping("/{userId}")
    fun getCardsByUserId(@PathVariable userId: Int): List<CardTo> {
        log.info("Microservice ${getMicroserviceName()} request cards of user $userId")
        return cardService.getCardsByUserId(userId)
                .map { cardMapper.asTo(it) }
    }
}