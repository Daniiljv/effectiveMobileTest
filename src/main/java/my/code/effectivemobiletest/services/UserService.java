package my.code.effectivemobiletest.services;

import com.expert.crmbackend.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto save(UserDto user);

    UserDto getById(Long id);

    List<UserDto> getAll();

    UserDto update(UserDto userDto);

    void block(Long userId);

    void unblock(Long userId);
}