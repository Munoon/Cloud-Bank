package munoon.bank.service.resource.user.user

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import lombok.NoArgsConstructor
import munoon.bank.common.user.UserRoles
import munoon.bank.service.resource.user.util.validator.ValidClass
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@KotlinBuilder
@NoArgsConstructor
data class AdminRegisterUserTo(
        @field:NotNull
        @field:NotEmpty
        @field:Length(min = 1, max = 20)
        val name: String,

        @field:NotNull
        @field:NotEmpty
        @field:Length(min = 1, max = 20)
        val surname: String,

        @field:NotNull
        @field:NotEmpty
        @field:Length(min = 3, max = 20)
        val username: String,

        @field:NotNull
        @field:NotEmpty
        @field:Length(min = 8)
        val password: String,

        @field:NotNull
        @field:NotEmpty
        @field:ValidClass
        @field:Length(min = 1, max = 20)
        @field:JsonProperty("class")
        val clazz: String,

        @field:NotNull
        val roles: Set<UserRoles>
) {
        override fun toString() =
                "AdminRegisterUserTo(name='$name', surname='$surname', username='$username', class='$clazz', roles=$roles)"
}

@KotlinBuilder
@NoArgsConstructor
data class AdminUpdateUserTo(
        @field:NotNull
        @field:NotEmpty
        @field:Length(min = 1, max = 20)
        val name: String,

        @field:NotNull
        @field:NotEmpty
        @field:Length(min = 1, max = 20)
        val surname: String,

        @field:NotNull
        @field:NotEmpty
        @field:Length(min = 3, max = 20)
        val username: String,

        @field:NotNull
        @field:NotEmpty
        @field:ValidClass
        @field:Length(min = 1, max = 20)
        @field:JsonProperty("class")
        var clazz: String,

        @field:NotNull
        val roles: Set<UserRoles>,
)

@NoArgsConstructor
data class AdminUpdateUserPasswordTo(
        @field:NotNull
        @field:NotEmpty
        @field:Length(min = 8)
        val password: String,
) {
        override fun toString() = "AdminUpdateUserPasswordTo()"
}

@NoArgsConstructor
data class UpdatePasswordTo(
        @field:NotNull
        @field:NotEmpty
        @field:Length(min = 8)
        val newPassword: String,

        val oldPassword: String
) {
        override fun toString() = "UpdatePasswordTo()"
}

@NoArgsConstructor
data class UpdateUsernameTo(
        val password: String,

        @field:NotNull
        @field:NotEmpty
        @field:Length(min = 3, max = 20)
        val newUsername: String
) {
        override fun toString() = "UpdateUsernameTo(newUsername='$newUsername')"
}