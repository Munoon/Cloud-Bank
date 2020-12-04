package munoon.bank.service.resource.user.user

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import lombok.NoArgsConstructor
import munoon.bank.common.user.UserRoles
import munoon.bank.service.resource.user.util.validator.ValidClass
import org.hibernate.validator.constraints.Length
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Entity
@KotlinBuilder
@NoArgsConstructor
@Table(name = "users")
data class UserEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int?,

        @field:NotNull
        @field:NotEmpty
        @field:Length(min = 1, max = 20)
        @Column(name = "name", nullable = false)
        var name: String,

        @field:NotNull
        @field:NotEmpty
        @field:Length(min = 1, max = 20)
        @Column(name = "surname", nullable = false)
        var surname: String,

        @field:NotNull
        @field:NotEmpty
        @field:Length(min = 3, max = 20)
        @Column(name = "username", nullable = false)
        var username: String,

        @field:NotNull
        @field:NotEmpty
        @field:Length(min = 8)
        @Column(name = "password", nullable = false)
        var password: String,

        @field:NotNull
        @Column(name = "registered", nullable = false, columnDefinition = "TIMESTAMP DEFAULT now() NOT NULL")
        var registered: LocalDateTime,

        @field:NotNull
        @field:NotEmpty
        @JsonProperty("class")
        @field:Length(min = 1, max = 20)
        @Column(name = "class", nullable = false)
        var clazz: String,

        @JsonInclude
        @field:NotNull
        @Enumerated(EnumType.STRING)
        @Column(name = "role", nullable = false)
        @ElementCollection(fetch = FetchType.EAGER)
        @CollectionTable(name = "users_role", joinColumns = [JoinColumn(name = "user_id")])
        val roles: Set<UserRoles>
) {
        override fun toString() =
                "UserEntity(id=$id, name='$name', surname='$surname', username='$username', registered=$registered, class='$clazz', roles=$roles)"
}