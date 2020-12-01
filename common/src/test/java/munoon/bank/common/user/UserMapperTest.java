package munoon.bank.common.user;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {
    @Test
    void asTo() {
        User user = new User(100, "Nikita", "Ivchenko", "munoon", "password", LocalDateTime.now(), Set.of(UserRoles.ROLE_ADMIN, UserRoles.ROLE_TEACHER));
        UserTo expected = new UserTo(100, "Nikita", "Ivchenko", "munoon", LocalDateTime.now(), Set.of(UserRoles.ROLE_ADMIN, UserRoles.ROLE_TEACHER));
        UserTo actual = UserMapper.INSTANCE.asTo(user);
        assertThat(actual).usingRecursiveComparison().ignoringFields("registered").isEqualTo(expected);
    }
}