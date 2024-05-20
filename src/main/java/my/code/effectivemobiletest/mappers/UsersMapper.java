package my.code.effectivemobiletest.mappers;

import my.code.effectivemobiletest.dtos.UserDto;
import my.code.effectivemobiletest.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsersMapper {
    UserDto toDto(UserEntity userEntity);
}
