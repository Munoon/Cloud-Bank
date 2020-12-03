package munoon.bank.service.resource.user.user

import com.github.pozo.KotlinBuilder
import lombok.NoArgsConstructor
import munoon.bank.common.user.UserRoles
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
        val roles: Set<UserRoles>
) {
    override fun toString() =
        "AdminRegisterUser(name='$name', surname='$surname', username='$username', roles=$roles)"
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
        val roles: Set<UserRoles>
)

@NoArgsConstructor
data class AdminUpdateUserPasswordTo(
        @field:NotNull
        @field:NotEmpty
        @field:Length(min = 8)
        val password: String,
)