package munook.bank.service.market.specimen

import munook.bank.service.market.product.ProductService
import munoon.bank.common.util.exception.NotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class SpecimenService(private val specimenRepository: SpecimenRepository,
                      private val productService: ProductService) {
    fun createSpecimen(saveSpecimenTo: SaveSpecimenTo): Specimen {
        val product = productService.getProductById(saveSpecimenTo.productId)
        val specimen = saveSpecimenTo.create(product)
        return specimenRepository.save(specimen)
    }

    fun updateSpecimen(id: Int, saveSpecimenTo: SaveSpecimenTo): Specimen {
        val specimen = getSpecimenById(id)
        val product = if (saveSpecimenTo.productId == specimen.product.id) specimen.product
                      else productService.getProductById(saveSpecimenTo.productId)
        specimen.update(saveSpecimenTo, product)
        return specimenRepository.save(specimen)
    }

    fun getSpecimenById(id: Int): Specimen = specimenRepository.findById(id)
        .orElseThrow { NotFoundException("Specimen with id '$id' is not found!") }

    fun getSpecimenByCustomId(customId: String): Specimen = specimenRepository.findByCustomId(customId)
        ?: throw NotFoundException("Specimen with custom id '$customId' is not found!")

    fun getAllSpecimensByProduct(pageable: Pageable, productId: Int, ableToBuy: Boolean): Page<Specimen> = when (ableToBuy) {
        true -> specimenRepository.findAllByProductIdAndAbleToBuyIsTrue(productId, pageable)
        false -> specimenRepository.findAllByProductId(productId, pageable)
    }
}