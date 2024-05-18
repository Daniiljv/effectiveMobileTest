package my.code.effectivemobiletest.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.code.effectivemobiletest.entities.BankAccountEntity;

import java.sql.Date;
import java.util.HashSet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    private BankAccountEntity bankAccount;

    private HashSet<String> phoneNumbers;

    private HashSet<String> emails;

    private Date dateOfBirth;

    private String fullName;

    private String username;

    private String password;

}