package munoon.bank.common.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")
public class User implements Serializable {
    private Integer id;

    private String name;

    private String surname;

    private String username;

    @JsonIgnore
    private String password;

    private LocalDateTime registered;

    private Set<UserRoles> roles;
}
