package munoon.bank.common.user;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserTo asTo(User user);

    FullUserTo asFullTo(User user);
}
