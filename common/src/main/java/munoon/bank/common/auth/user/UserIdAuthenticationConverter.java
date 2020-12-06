package munoon.bank.common.auth.user;

import munoon.bank.common.AuthorizedUser;

import java.util.Map;

public abstract class UserIdAuthenticationConverter extends CustomUserAuthenticationConverter {
    @Override
    public AuthorizedUser getAuthorizedUser(Map<String, ?> map) {
        if (map.containsKey(CustomUserAuthenticationConverter.USER_ID_KEY)) {
            int userId = (int) map.get(CustomUserAuthenticationConverter.USER_ID_KEY);
            return getAuthorizedUser(userId);
        }
        return null;
    }

    public abstract AuthorizedUser getAuthorizedUser(int userId);
}
