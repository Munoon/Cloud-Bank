package munoon.bank.service.resource.user.user

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.readValue
import munoon.bank.common.user.User
import munoon.bank.common.user.UserRoles
import munoon.bank.common.user.UserTo
import munoon.bank.service.resource.user.util.JsonUtils
import munoon.bank.service.resource.user.util.JsonUtils.getContent
import org.assertj.core.api.Assertions.assertThat
import org.springframework.test.web.servlet.ResultMatcher
import java.time.LocalDateTime

object UserTestData {
    const val USER_ID = 100
    const val USER_PASSWORD = "password"
    const val USER_CLASS = "10"
    val DEFAULT_USER_ENTITY = UserEntity(USER_ID, "Nikita", "Ivchenko", "munoon", "{noop}$USER_PASSWORD", LocalDateTime.now(), USER_CLASS, hashSetOf(UserRoles.ROLE_ADMIN, UserRoles.ROLE_BARMEN, UserRoles.ROLE_COURIER, UserRoles.ROLE_TEACHER))
    val DEFAULT_USER = User(USER_ID, "Nikita", "Ivchenko", "munoon", "{noop}password", USER_CLASS, LocalDateTime.now(), hashSetOf(UserRoles.ROLE_ADMIN, UserRoles.ROLE_BARMEN, UserRoles.ROLE_COURIER, UserRoles.ROLE_TEACHER))

    fun contentJson(expected: UserTo) = ResultMatcher {
        val actual = JsonUtils.readFromJson(it, UserTo::class.java)
        assertMatch(actual, expected)
    }

    fun contentJson(expected: UserToWithCards) = ResultMatcher {
        val actual = JsonUtils.readFromJson(it, UserToWithCards::class.java)
        assertMatch(actual, expected)
    }

    fun contentJsonPage(vararg expected: UserTo) = ResultMatcher {
        val node = JsonUtils.OBJECT_MAPPER.readTree(getContent(it)).at("/content")
        val actual = JsonUtils.OBJECT_MAPPER.readValue<List<UserTo>>(node.toString())
        assertMatch(actual, *expected)
    }

    fun contentJsonList(vararg expected: UserTo) = ResultMatcher {
        val actual = JsonUtils.OBJECT_MAPPER.readValue(getContent(it), object : TypeReference<List<UserTo>>() {})
        assertMatch(actual, *expected)
    }

    fun assertMatch(actual: User, expected: User) {
        assertThat(actual).usingRecursiveComparison().ignoringFields("registered", "password").isEqualTo(expected)
    }

    fun assertMatch(actual: UserEntity, expected: UserEntity) {
        assertThat(actual).usingRecursiveComparison().ignoringFields("registered", "password").isEqualTo(expected)
    }

    fun assertMatch(actual: UserTo, expected: UserTo) {
        assertThat(actual).usingRecursiveComparison().ignoringFields("registered").isEqualTo(expected)
    }

    fun assertMatch(actual: UserToWithCards, expected: UserToWithCards) {
        assertThat(actual).usingRecursiveComparison().ignoringFields("registered").isEqualTo(expected)
    }

    fun assertMatch(actual: List<User>, expected: List<User>) {
        assertThat(actual).usingElementComparatorIgnoringFields("registered", "password").isEqualTo(expected)
    }

    fun assertMatch(actual: List<User>, vararg expected: User) {
        assertMatch(actual, expected.toList())
    }

    fun assertMatchTo(actual: List<UserTo>, expected: List<UserTo>) {
        assertThat(actual).usingElementComparatorIgnoringFields("registered").isEqualTo(expected.toList())
    }

    fun assertMatch(actual: List<UserTo>, vararg expected: UserTo) {
        assertMatchTo(actual, expected.toList())
    }
}