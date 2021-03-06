package munook.bank.service.market.product

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page
import java.time.LocalDateTime

@Mapper(imports = [LocalDateTime::class])
interface ProductMapper {
    @Mappings(
        Mapping(target = "id", expression = "java(null)"),
        Mapping(target = "created", expression = "java(LocalDateTime.now())")
    )
    fun create(saveProductTo: SaveProductTo): Product

    fun update(saveProductTo: SaveProductTo, @MappingTarget product: Product): Product

    fun asTo(product: Product): ProductTo

    companion object {
        val INSTANCE: ProductMapper = Mappers.getMapper(ProductMapper::class.java)
    }
}

fun Product.asTo() = ProductMapper.INSTANCE.asTo(this)
fun Page<Product>.asTo() = map { it.asTo() }
fun Product.update(saveProductTo: SaveProductTo) = ProductMapper.INSTANCE.update(saveProductTo, this)
fun SaveProductTo.asProduct() = ProductMapper.INSTANCE.create(this)