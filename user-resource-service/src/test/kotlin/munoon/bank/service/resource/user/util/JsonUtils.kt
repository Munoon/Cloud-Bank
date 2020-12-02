package munoon.bank.service.resource.user.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.test.web.servlet.MvcResult
import java.io.IOException

object JsonUtils {
    private val OBJECT_MAPPER = ObjectMapper().apply {
        registerModule(JavaTimeModule())
    }

    fun <T> readValue(json: String, clazz: Class<T>?): T {
        return try {
            OBJECT_MAPPER.readValue(json, clazz)
        } catch (e: IOException) {
            throw IllegalArgumentException("Invalid read from JSON:\n'$json'", e)
        }
    }

    fun <T> readFromJson(mvcResult: MvcResult, clazz: Class<T>): T = readValue(getContent(mvcResult), clazz)
    fun getContent(result: MvcResult): String = result.response.contentAsString
}