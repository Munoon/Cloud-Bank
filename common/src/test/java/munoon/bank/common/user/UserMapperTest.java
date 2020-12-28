package munoon.bank.common.user;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {
    @Test
    void asTo() {
        User user = new User(100, "Nikita", "Ivchenko", "munoon", "password", "10", 100.0, LocalDateTime.now(), Set.of(UserRoles.ROLE_ADMIN, UserRoles.ROLE_TEACHER));
        UserTo expected = new UserTo(100, "Nikita", "Ivchenko", "munoon", "10", LocalDateTime.now(), Set.of(UserRoles.ROLE_ADMIN, UserRoles.ROLE_TEACHER));
        UserTo actual = UserMapper.INSTANCE.asTo(user);
        assertThat(actual).usingRecursiveComparison().ignoringFields("registered").isEqualTo(expected);
    }

    @Test
    void asFullTo() {
        User user = new User(100, "Nikita", "Ivchenko", "munoon", "password", "10", 100.0, LocalDateTime.now(), Set.of(UserRoles.ROLE_ADMIN, UserRoles.ROLE_TEACHER));
        FullUserTo expected = new FullUserTo(100, "Nikita", "Ivchenko", "munoon", "10", 100.0, LocalDateTime.now(), Set.of(UserRoles.ROLE_ADMIN, UserRoles.ROLE_TEACHER));
        FullUserTo actual = UserMapper.INSTANCE.asFullTo(user);
        assertThat(actual).usingRecursiveComparison().ignoringFields("registered").isEqualTo(expected);
    }
}