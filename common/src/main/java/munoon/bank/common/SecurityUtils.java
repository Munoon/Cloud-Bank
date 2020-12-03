package munoon.bank.common;

import munoon.bank.common.user.UserTo;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import static org.springframework.util.Assert.notNull;

public class SecurityUtils {
    private static AuthorizedUser safeGet() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        notNull(authentication, "User not authenticated");
        if (authentication instanceof OAuth2Authentication &&
                ((OAuth2Authentication) authentication).getUserAuthentication() instanceof UsernamePasswordAuthenticationToken
                && ((OAuth2Authentication) authentication).getUserAuthentication().getPrincipal() instanceof AuthorizedUser) {
            return (AuthorizedUser) ((OAuth2Authentication) authentication).getUserAuthentication().getPrincipal();
        } else if (authentication instanceof UsernamePasswordAuthenticationToken
                && authentication.getPrincipal() instanceof AuthorizedUser) {
            return (AuthorizedUser) authentication.getPrincipal();
        } else {
            throw new IllegalStateException("Authentication incorrect");
        }
    }

    public static UserTo authUser() {
        return safeGet().getUser();
    }

    public static int authUserId() {
        return authUser().getId();
    }
}
