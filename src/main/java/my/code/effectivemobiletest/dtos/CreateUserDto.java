package my.code.effectivemobiletest.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CreateUserDto {
    private String username;
    private String password;
    private BigDecimal startBalance;
    private String phoneNumber;
    private String email;
}
