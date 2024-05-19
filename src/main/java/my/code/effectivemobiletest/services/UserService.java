package my.code.effectivemobiletest.services;


import my.code.effectivemobiletest.dtos.CreateUserDto;
import my.code.effectivemobiletest.dtos.UserDto;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public interface UserService {
    UserDto create(CreateUserDto createUserDto);
    String addPhoneNumber(String phoneNumberToAdd);
    String changePhoneNumber(String numberForChange, String newNumber);
    String addEmail(String emailToAdd);
    String changeEmail(String emailForChange, String newEmail);
    String deletePhoneNumber(String numberToDelete);
    String deleteEmail(String emailToDelete);
    List<UserDto> findByFilters(Date dateOfBirth,
                                String phoneNumber,
                                String fullName,
                                String email);

}