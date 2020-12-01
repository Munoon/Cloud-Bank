package munoon.bank.service.auth.user

import com.fasterxml.jackson.annotation.JsonInclude
import lombok.NoArgsConstructor
import munoon.bank.common.user.UserRoles
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@NoArgsConstructor
@Table(name = "users")
data class UserEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int?,

        @Column(name = "name", nullable = false)
        val name: String,

        @Column(name = "surname", nullable = false)
        val surname: String,

        @Column(name = "username", nullable = false)
        val username: String,

        @Column(name = "password", nullable = false)
        val password: String,

        @Column(name = "registered", nullable = false, columnDefinition = "TIMESTAMP DEFAULT now() NOT NULL")
        val registered: LocalDateTime,

        @JsonInclude
        @Enumerated(EnumType.STRING)
        @Column(name = "role", nullable = false)
        @CollectionTable(name = "users_role", joinColumns = [JoinColumn(name = "user_id")])
        @ElementCollection(fetch = FetchType.EAGER)
        val roles: Set<UserRoles>
) {
        override fun toString() =
                "UserEntity(id=$id, name='$name', surname='$surname', username='$username', registered=$registered, roles=$roles)"
}