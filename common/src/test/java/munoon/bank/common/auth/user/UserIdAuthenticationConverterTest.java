package munoon.bank.common.auth.user;

import munoon.bank.common.AuthorizedUser;
import munoon.bank.common.user.UseTestData;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UserIdAuthenticationConverterTest {
    @Test
    void convertUserAuthentication() {
        var authUser = new AuthorizedUser(UseTestData.USER);
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

        AuthorizedUser authorizedUser = new AuthorizedUser(UseTestData.USER);
        var token = new UsernamePasswordAuthenticationToken(authorizedUser, authorizedUser.getPassword(), authentication.getAuthorities());

        assertThat(authentication).usingRecursiveComparison().isEqualTo(token);
    }

    class TestConverter extends UserIdAuthenticationConverter {
        @Override
        public AuthorizedUser getAuthorizedUser(int userId) {
            if (userId == 100) {
                return new AuthorizedUser(UseTestData.USER);
            }
            return null;
        }
    }
}