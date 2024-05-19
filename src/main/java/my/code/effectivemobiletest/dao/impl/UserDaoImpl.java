package my.code.effectivemobiletest.dao.impl;

import lombok.RequiredArgsConstructor;
import my.code.effectivemobiletest.configs.DatabaseConfig;
import my.code.effectivemobiletest.dao.UserDao;
import my.code.effectivemobiletest.dtos.UserDto;
import my.code.effectivemobiletest.entities.BankAccountEntity;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final DatabaseConfig databaseConfig;

    @Override
    public List<UserDto> findByFilters(Date dateOfBirth,
                                       String phoneNumber,
                                       String fullName,
                                       String email) {

        List<UserDto> userDtoList = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();


        StringBuilder query = new StringBuilder("""
                SELECT *
                FROM users u
                LEFT JOIN bank_accounts ba ON u.bank_account_id = ba.id
                WHERE 1=1
                """);

        if (dateOfBirth != null) {
            query.append(" AND u.date_of_birth > ?");
            parameters.add(dateOfBirth);
        }

        if (phoneNumber != null) {
            query.append(" AND ? = ANY(u.phone_numbers)");
            parameters.add(phoneNumber);
        }

        if (fullName != null) {
            query.append(" AND u.full_name LIKE ?");
            parameters.add(fullName + "%");
        }

        if (email != null) {
            query.append(" AND ? = ANY(u.emails)");

            parameters.add(email);
        }

        try (Connection connection = databaseConfig.connection();
             PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setObject(i + 1, parameters.get(i));
            }

            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    UserDto userDto = new UserDto();

                    userDto.setId(resultSet.getLong("id"));

                    BankAccountEntity bankAccount = new BankAccountEntity();
                    bankAccount.setId(resultSet.getLong("bank_account_id"));
                    bankAccount.setBalance(resultSet.getBigDecimal("balance"));
                    userDto.setBankAccount(bankAccount);

                    userDto.setDateOfBirth(resultSet.getDate("date_of_birth"));
                    userDto.setFullName(resultSet.getString("full_name"));
                    userDto.setUsername(resultSet.getString("username"));
                    userDto.setPassword(resultSet.getString("password"));

                    String phoneNumbersString = resultSet.getString("phone_numbers");
                    String emailsString = resultSet.getString("emails");

                    HashSet<String> phoneNumbers = new HashSet<>(Arrays.asList(phoneNumbersString.split(",")));
                    HashSet<String> emails = new HashSet<>(Arrays.asList(emailsString.split(",")));

                    userDto.setPhoneNumbers(phoneNumbers);
                    userDto.setEmails(emails);

                    userDtoList.add(userDto);

                }
            }
        } catch (SQLException sqlException){
            throw new RuntimeException(sqlException.getMessage());
        }

        return userDtoList;
    }
}
