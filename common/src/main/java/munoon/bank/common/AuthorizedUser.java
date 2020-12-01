package munoon.bank.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import munoon.bank.common.user.User;
import munoon.bank.common.user.UserMapper;
import munoon.bank.common.user.UserTo;

@EqualsAndHashCode(callSuper = false)
public class AuthorizedUser extends org.springframework.security.core.userdetails.User {
    @Getter
    private final UserTo user;

    public AuthorizedUser(UserTo user) {
        super(user.getUsername(), "N/A", user.getRoles());
        this.user = user;
    }

    public AuthorizedUser(User user) {
        super(user.getUsername(), user.getPassword(), user.getRoles());
        this.user = UserMapper.INSTANCE.asTo(user);
    }

    public int getId() {
        return user.getId();
    }
}
