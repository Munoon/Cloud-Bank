package munoon.bank.service.transactional.util

object MoneyUtils {
    fun countWithTax(count: Double, tax: Double, taxType: TaxType): Double {
        val percent = (count * tax) / 100
        return when (taxType) {
            TaxType.PLUS -> count + percent
            TaxType.MINUS -> count - percent
        }
    }

    enum class TaxType {
        PLUS, MINUS
    }
}