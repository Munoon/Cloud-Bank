package munook.bank.service.market.util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.test.web.servlet.MvcResult
import java.io.IOException
import kotlin.reflect.KClass

object JsonUtil {
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

    fun <T: Any> readFromJson(mvcResult: MvcResult, clazz: KClass<T>) = readValue(getContent(mvcResult), clazz.java)
    fun getContent(result: MvcResult): String = result.response.contentAsString
}