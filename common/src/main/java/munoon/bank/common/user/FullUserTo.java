package munoon.bank.common.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FullUserTo implements Serializable {
    private Integer id;

    private String name;

    private String surname;

    private String username;

    @JsonProperty("class")
    private String clazz;

    private Double salary;

    private LocalDateTime registered;

    private Set<UserRoles> roles;
}
