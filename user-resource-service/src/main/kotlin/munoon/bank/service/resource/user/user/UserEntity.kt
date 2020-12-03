package munoon.bank.service.resource.user.user

import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import lombok.NoArgsConstructor
import munoon.bank.common.user.UserRoles
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@KotlinBuilder
@NoArgsConstructor
@Table(name = "users")
data class UserEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int?,

        @Column(name = "name", nullable = false)
        var name: String,

        @Column(name = "surname", nullable = false)
        var surname: String,

        @Column(name = "username", nullable = false)
        var username: String,

        @Column(name = "password", nullable = false)
        var password: String,

        @Column(name = "registered", nullable = false, columnDefinition = "TIMESTAMP DEFAULT now() NOT NULL")
        var registered: LocalDateTime,

        @JsonInclude
        @Enumerated(EnumType.STRING)
        @Column(name = "role", nullable = false)
        @ElementCollection(fetch = FetchType.EAGER)
        @CollectionTable(name = "users_role", joinColumns = [JoinColumn(name = "user_id")])
        val roles: Set<UserRoles>
) {
        override fun toString() =
                "UserEntity(id=$id, name='$name', surname='$surname', username='$username', registered=$registered, roles=$roles)"
}