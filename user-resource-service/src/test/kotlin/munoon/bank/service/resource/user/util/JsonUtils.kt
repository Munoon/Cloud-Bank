package munoon.bank.service.resource.user.util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.assertj.core.api.Assertions.assertThat
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultMatcher
import java.io.IOException

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

    fun <T> contentJsonList(vararg obj: T) = ResultMatcher {
        val data = OBJECT_MAPPER.readValue(getContent(it), object : TypeReference<List<T>>() {})
        assertThat(data).usingDefaultComparator().isEqualTo(obj.toList())
    }

    fun emptyJsonPage() = ResultMatcher {
        val node = OBJECT_MAPPER.readTree(getContent(it)).at("/content")
        assertThat(node.size()).isEqualTo(0)
    }
}