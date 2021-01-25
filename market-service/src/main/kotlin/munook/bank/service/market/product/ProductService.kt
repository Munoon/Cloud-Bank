package munook.bank.service.market.product

import munoon.bank.common.util.exception.NotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ProductService(private val productRepository: ProductRepository) {
    fun createProduct(saveProductTo: SaveProductTo): Product {
        val product = saveProductTo.asProduct()
        return productRepository.save(product)
    }

    fun updateProduct(id: Int, saveProductTo: SaveProductTo): Product {
        val product = getProductById(id)
        val updated = product.update(saveProductTo)
        return productRepository.save(updated)
    }

    fun getProductById(id: Int): Product = productRepository.findById(id)
        .orElseThrow { NotFoundException("Product with id $id is not found!") }

    fun getProducts(pageable: Pageable, ableToBuyOnly: Boolean): Page<Product> =
        if (ableToBuyOnly) productRepository.findAllByAbleToBuyIsTrue(pageable) else productRepository.findAll(pageable)
}