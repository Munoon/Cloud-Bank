package munook.bank.service.market.controller

import munook.bank.service.market.product.ProductService
import munook.bank.service.market.product.ProductTo
import munook.bank.service.market.product.SaveProductTo
import munook.bank.service.market.product.asTo
import munoon.bank.common.SecurityUtils.authUserId
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/product")
@PreAuthorize("hasRole('ROLE_ADMIN')")
class ProductController(private val productService: ProductService) {
    private val log = LoggerFactory.getLogger(ProductController::class.java)

    @PostMapping
    fun createProduct(@Valid  @RequestBody saveProductTo: SaveProductTo): ProductTo {
        log.info("Admin ${authUserId()} create product: $saveProductTo")
        return productService.createProduct(saveProductTo).asTo()
    }

    @PutMapping("/{id}")
    fun updateProduct(@PathVariable id: Int, @Valid @RequestBody saveProductTo: SaveProductTo): ProductTo {
        log.info("Admin ${authUserId()} update product with id $id: $saveProductTo")
        return productService.updateProduct(id, saveProductTo).asTo()
    }

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Int): ProductTo {
        log.info("Admin ${authUserId()} get product with id $id")
        return productService.getProductById(id).asTo()
    }

    @GetMapping
    fun getProducts(pageable: Pageable, @RequestParam(defaultValue = "true") ableToBuyOnly: Boolean): Page<ProductTo> {
        log.info("Admin ${authUserId()} get products (ableToBuyOnly = $ableToBuyOnly): $pageable")
        return productService.getProducts(pageable, ableToBuyOnly).asTo()
    }
}