package munoon.bank.service.resource.user.util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.readValue
import com.jayway.jsonpath.JsonPath
import munoon.bank.common.user.UserTo
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.test.web.servlet.MvcResult
import java.io.IOException
import java.lang.IllegalStateException

object JsonUtils {
    val OBJECT_MAPPER = ObjectMapper().apply {
        registerModule(JavaTimeModule())
    }

    fun <T> writeValue(obj: T): String = try {
        OBJECT_MAPPER.writeValueAsString(obj)
    } catch (e: JsonProcessingException) {
        throw IllegalStateException("Invalid write to JSON: $obj", e)
    }

    fun <T> readValue(json: String, clazz: Class<T>): T = try {
        OBJECT_MAPPER.readValue(json, clazz)
    } catch (e: IOException) {
        throw IllegalArgumentException("Invalid read from JSON:\n'$json'", e)
    }

    fun <T> readFromJson(mvcResult: MvcResult, clazz: Class<T>) = readValue(getContent(mvcResult), clazz)
    fun getContent(result: MvcResult): String = result.response.contentAsString
}