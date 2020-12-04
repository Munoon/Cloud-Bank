package munoon.bank.common.user;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("class")
    private String clazz;

    private LocalDateTime registered;

    private Set<UserRoles> roles;
}
