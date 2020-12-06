package munoon.bank.common.auth.user;

import munoon.bank.common.AuthorizedUser;
import munoon.bank.common.user.UseTestData;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TokenAuthenticationConverterTest {
    private static final String CORRECT_TOKEN = "DEFAULT_USER";

    @Test
    void extractAuthentication() {
        TestConverter converter = new TestConverter();
        Authentication authentication = converter.extractAuthentication(Map.of(TokenAuthenticationConverter.TOKEN_KEY, CORRECT_TOKEN));

        AuthorizedUser authorizedUser = new AuthorizedUser(UseTestData.USER);
        var token = new UsernamePasswordAuthenticationToken(authorizedUser, authorizedUser.getPassword(), authentication.getAuthorities());

        assertThat(authentication).usingRecursiveComparison().isEqualTo(token);
    }

    class TestConverter extends TokenAuthenticationConverter {
        @Override
        public AuthorizedUser getAuthorizedUser(String token) {
            if (token.equals(CORRECT_TOKEN)) {
                return new AuthorizedUser(UseTestData.USER);
            }
            return null;
        }
    }
}