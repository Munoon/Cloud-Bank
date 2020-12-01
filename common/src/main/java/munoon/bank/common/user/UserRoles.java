package munoon.bank.common.user;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

public enum UserRoles implements GrantedAuthority, Serializable {
    ROLE_ADMIN, ROLE_BARMEN, ROLE_COURIER, ROLE_TEACHER;

    @Override
    public String getAuthority() {
        return name();
    }
}
