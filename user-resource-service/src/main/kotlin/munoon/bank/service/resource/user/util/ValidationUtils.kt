package munoon.bank.service.resource.user.util

import org.springframework.validation.FieldError
import javax.validation.ConstraintViolation

object ValidationUtils {
    fun getErrorFieldMap(fieldErrors: List<FieldError>): Map<String, List<String?>> {
        val map = HashMap<String, MutableList<String?>>()
        fieldErrors.forEach {
            map.computeIfAbsent(it.field) { ArrayList() }
            map[it.field]!!.add(it.defaultMessage)
        }
        return map
    }

    fun getErrorFieldMap(fieldErrors: Set<ConstraintViolation<*>>): Map<String, List<String?>> {
        val map = HashMap<String, MutableList<String?>>()
        fieldErrors.forEach {
            val field = it.propertyPath.toString()
            map.computeIfAbsent(field) { ArrayList() }
            map[field]!!.add(it.message)
        }
        return map
    }
}