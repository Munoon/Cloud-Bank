package munook.bank.service.market.controller

import munook.bank.service.market.specimen.SaveSpecimenTo
import munook.bank.service.market.specimen.SpecimenService
import munook.bank.service.market.specimen.SpecimenTo
import munook.bank.service.market.specimen.asTo
import munoon.bank.common.SecurityUtils.authUserId
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/specimen")
@PreAuthorize("hasRole('ROLE_ADMIN')")
class SpecimenController(private val specimenService: SpecimenService) {
    private val log = LoggerFactory.getLogger(SpecimenController::class.java)

    @PostMapping
    fun createSpecimen(@Valid @RequestBody saveSpecimenTo: SaveSpecimenTo): SpecimenTo {
        log.info("Admin ${authUserId()} create specimen: $saveSpecimenTo")
        return specimenService.createSpecimen(saveSpecimenTo).asTo()
    }

    @PutMapping("/{id}")
    fun updateSpecimen(@PathVariable id: Int, @Valid @RequestBody saveSpecimenTo: SaveSpecimenTo): SpecimenTo {
        log.info("Admin ${authUserId()} update specimen with id $id: $saveSpecimenTo")
        return specimenService.updateSpecimen(id, saveSpecimenTo).asTo()
    }

    @GetMapping("/{id}")
    fun getSpecimenById(@PathVariable id: Int): SpecimenTo {
        log.info("Admin ${authUserId()} get specimen with id $id")
        return specimenService.getSpecimenById(id).asTo()
    }

    @GetMapping("/custom/{id}")
    fun getSpecimenByCustomId(@PathVariable id: String): SpecimenTo {
        log.info("Admin ${authUserId()} get specimen with custom id '$id'")
        return specimenService.getSpecimenByCustomId(id).asTo()
    }

    @GetMapping
    fun getAllSpecimen(pageable: Pageable,
                       @RequestParam productId: Int,
                       @RequestParam(defaultValue = "true") ableToBuyOnly: Boolean): Page<SpecimenTo> {
        log.info("Admin ${authUserId()} request all specimens (ableToBuyOnly = $ableToBuyOnly): $pageable")
        return specimenService.getAllSpecimensByProduct(pageable, productId, ableToBuyOnly).asTo()
    }
}