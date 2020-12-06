package munoon.bank.common.user;

import java.time.LocalDateTime;
import java.util.Set;

public class UseTestData {
    public static final User USER = new User(100, "Nikita", "Ivcheko", "munoon", "{noop}password", "10", LocalDateTime.now(), Set.of(UserRoles.ROLE_ADMIN, UserRoles.ROLE_TEACHER, UserRoles.ROLE_BARMEN, UserRoles.ROLE_COURIER));
}
