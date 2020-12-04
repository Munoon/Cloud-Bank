package munoon.bank.common;

import munoon.bank.common.user.User;
import munoon.bank.common.user.UserRoles;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CustomUserAuthenticationConverterTest {
    private static final User USER = new User(100, "Nikita", "Ivcheko", "munoon", "{noop}password", "10", LocalDateTime.now(), Set.of(UserRoles.ROLE_ADMIN, UserRoles.ROLE_TEACHER, UserRoles.ROLE_BARMEN, UserRoles.ROLE_COURIER));

    @Test
    void convertUserAuthentication() {
        var authUser = new AuthorizedUser(USER);
        var token = new UsernamePasswordAuthenticationToken(authUser, "N/A", authUser.getAuthorities());

        CustomUserAuthenticationConverter converter = new TestConverter();
        Map<String, ?> map = converter.convertUserAuthentication(token);
        assertThat(map).isEqualTo(Collections.singletonMap("user_id", 100));
    }

    @Test
    void extractAuthentication() {
        Map<String, Integer> map = Collections.singletonMap("user_id", 100);
        CustomUserAuthenticationConverter converter = new TestConverter();
        Authentication authentication = converter.extractAuthentication(map);

        AuthorizedUser authorizedUser = new AuthorizedUser(USER);
        var token = new UsernamePasswordAuthenticationToken(authorizedUser, authorizedUser.getPassword(), authentication.getAuthorities());

        assertThat(authentication).usingRecursiveComparison().isEqualTo(token);
    }

    class TestConverter extends CustomUserAuthenticationConverter {
        @Override
        public AuthorizedUser getAuthorizedUser(int userId) {
            if (userId == 100) {
                return new AuthorizedUser(USER);
            }
            return null;
        }
    }
}