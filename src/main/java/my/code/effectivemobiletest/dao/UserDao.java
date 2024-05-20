package my.code.effectivemobiletest.dao;

import my.code.effectivemobiletest.dtos.UserDto;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
@Service
public interface UserDao {
List<UserDto> findByFilters(Date dateOfBirth,
                            String phoneNumber,
                            String fullName,
                            String email,
                            int page,
                            int pageSize);
}
