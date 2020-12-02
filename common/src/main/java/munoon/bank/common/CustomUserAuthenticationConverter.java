package munoon.bank.common;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;

import java.util.Collections;
import java.util.Map;

public abstract class CustomUserAuthenticationConverter implements UserAuthenticationConverter {
    private static final String USER_ID_KEY = "user_id";

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
        if (map.containsKey(USER_ID_KEY)) {
            int userId = (int) map.get(USER_ID_KEY);
            AuthorizedUser user = getAuthorizedUser(userId);
            return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        }
        return null;
    }

    public abstract AuthorizedUser getAuthorizedUser(int userId);
}
