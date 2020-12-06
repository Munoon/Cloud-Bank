package munoon.bank.common.auth.user;

import munoon.bank.common.AuthorizedUser;

import java.util.Map;

public abstract class TokenAuthenticationConverter extends CustomUserAuthenticationConverter {
    public static final String TOKEN_KEY = "jwt_token";

    @Override
    public AuthorizedUser getAuthorizedUser(Map<String, ?> map) {
        if (map.containsKey(TOKEN_KEY)) {
            String token = (String) map.get(TOKEN_KEY);
            return getAuthorizedUser(token);
        }
        return null;
    }

    public abstract AuthorizedUser getAuthorizedUser(String token);
}
