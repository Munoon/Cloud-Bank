package munoon.bank.common.auth.token;

import munoon.bank.common.auth.user.TokenAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.Map;

public class CustomJwtAccessTokenConverter extends JwtAccessTokenConverter {
    @Override
    protected Map<String, Object> decode(String token) {
        Map<String, Object> decode = super.decode(token);
        decode.put(TokenAuthenticationConverter.TOKEN_KEY, token);
        return decode;
    }
}
