package munoon.bank.common.auth.user;

import munoon.bank.common.AuthorizedUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;

import java.util.Collections;
import java.util.Map;

public abstract class CustomUserAuthenticationConverter implements UserAuthenticationConverter {
    public static final String USER_ID_KEY = "user_id";

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication userAuthentication) {
        if (userAuthentication instanceof UsernamePasswordAuthenticationToken
                && userAuthentication.getPrincipal() instanceof AuthorizedUser) {
            int userId = ((AuthorizedUser) userAuthentication.getPrincipal()).getId();
            return Collections.singletonMap(USER_ID_KEY, userId);
        }
        return null;
    }

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        AuthorizedUser user = getAuthorizedUser(map);
        return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    }

    public abstract AuthorizedUser getAuthorizedUser(Map<String, ?> map);
}
