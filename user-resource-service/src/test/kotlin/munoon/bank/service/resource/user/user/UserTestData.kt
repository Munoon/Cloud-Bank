package munoon.bank.service.resource.user.user

import com.fasterxml.jackson.module.kotlin.readValue
import munoon.bank.common.user.User
import munoon.bank.common.user.UserRoles
import munoon.bank.common.user.UserTo
import munoon.bank.service.resource.user.util.JsonUtils
import munoon.bank.service.resource.user.util.JsonUtils.getContent
import org.assertj.core.api.Assertions.assertThat
import org.springframework.data.domain.Page
import org.springframework.test.web.servlet.ResultMatcher
import java.time.LocalDateTime

object UserTestData {
    const val USER_ID = 100
    val DEFAULT_USER_ENTITY = UserEntity(USER_ID, "Nikita", "Ivchenko", "munoon", "{noop}password", LocalDateTime.now(), hashSetOf(UserRoles.ROLE_ADMIN, UserRoles.ROLE_BARMEN, UserRoles.ROLE_COURIER, UserRoles.ROLE_TEACHER))
    val DEFAULT_USER = User(USER_ID, "Nikita", "Ivchenko", "munoon", "{noop}password", LocalDateTime.now(), hashSetOf(UserRoles.ROLE_ADMIN, UserRoles.ROLE_BARMEN, UserRoles.ROLE_COURIER, UserRoles.ROLE_TEACHER))

    fun contentJson(expected: UserTo) = ResultMatcher {
        val actual = JsonUtils.readFromJson(it, UserTo::class.java)
        assertThat(actual).usingRecursiveComparison().ignoringFields("registered").isEqualTo(expected)
    }

    fun contentJsonPage(vararg expected: UserTo) = ResultMatcher {
        val node = JsonUtils.OBJECT_MAPPER.readTree(getContent(it)).at("/content")
        val actual = JsonUtils.OBJECT_MAPPER.readValue<List<UserTo>>(node.toString())
        assertThat(actual).usingElementComparatorIgnoringFields("registered").isEqualTo(expected.toList())
    }
}