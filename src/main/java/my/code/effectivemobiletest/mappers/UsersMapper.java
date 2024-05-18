package my.code.effectivemobiletest.mappers;

import my.code.effectivemobiletest.dtos.CreateUserDto;
import my.code.effectivemobiletest.dtos.UserDto;
import my.code.effectivemobiletest.entities.UserEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsersMapper {
    UserEntity toEntity(UserDto userDto);
    UserDto toDto(UserEntity userEntity);
    List<UserDto> toDtoList(List<UserEntity> userEntityList);
}
