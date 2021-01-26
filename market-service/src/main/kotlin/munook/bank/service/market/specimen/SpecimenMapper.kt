package munook.bank.service.market.specimen

import munook.bank.service.market.product.Product
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers
import java.time.LocalDateTime

@Mapper(imports = [LocalDateTime::class])
interface SpecimenMapper {
    @Mappings(
        Mapping(target = "id", expression = "java(null)"),
        Mapping(target = "created", expression = "java(LocalDateTime.now())"),
        Mapping(target = "product", source = "product"),
        Mapping(target = "ableToBuy", source = "saveSpecimenTo.ableToBuy")
    )
    fun create(saveSpecimenTo: SaveSpecimenTo, product: Product): Specimen

    @Mappings(
        Mapping(target = "product", source = "product"),
        Mapping(target = "ableToBuy", source = "saveSpecimenTo.ableToBuy"),
        Mapping(target = "id", ignore = true),
        Mapping(target = "created", ignore = true)
    )
    fun update(saveSpecimenTo: SaveSpecimenTo, product: Product, @MappingTarget specimen: Specimen): Specimen

    companion object {
        val INSTANCE: SpecimenMapper = Mappers.getMapper(SpecimenMapper::class.java)
    }
}

fun SaveSpecimenTo.create(product: Product) = SpecimenMapper.INSTANCE.create(this, product)
fun Specimen.update(saveSpecimenTo: SaveSpecimenTo, product: Product) = SpecimenMapper.INSTANCE.update(saveSpecimenTo, product, this)