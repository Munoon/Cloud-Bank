package munoon.bank.common.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("class")
    private String clazz;

    private Double salary;

    private LocalDateTime registered;

    private Set<UserRoles> roles;

    public User(User u) {
        this(u.getId(), u.getName(), u.getSurname(), u.getUsername(), u.getPassword(), u.getClazz(), u.getSalary(), LocalDateTime.from(u.getRegistered()), Set.copyOf(u.getRoles()));
    }
}
