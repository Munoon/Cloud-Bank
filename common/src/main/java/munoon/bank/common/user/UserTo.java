package munoon.bank.common.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTo {
    private Integer id;

    private String name;

    private String surname;

    private String username;

    private LocalDateTime registered;

    private Set<UserRoles> roles;
}
