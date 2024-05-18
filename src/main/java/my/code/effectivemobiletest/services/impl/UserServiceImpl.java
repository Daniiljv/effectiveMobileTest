package my.code.effectivemobiletest.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import my.code.effectivemobiletest.dao.UserDao;
import my.code.effectivemobiletest.dtos.CreateUserDto;
import my.code.effectivemobiletest.dtos.UserDto;
import my.code.effectivemobiletest.entities.BankAccountEntity;
import my.code.effectivemobiletest.entities.UserEntity;
import my.code.effectivemobiletest.mappers.UsersMapper;
import my.code.effectivemobiletest.repositories.BankAccountRepo;
import my.code.effectivemobiletest.repositories.UserRepo;
import my.code.effectivemobiletest.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepo userRepo;
    private final BankAccountRepo bankAccountRepo;
    private final UserDao userDao;
    private final UsersMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User %s is not found", username));
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public UserDto create(CreateUserDto createUserDto) {
        try {
            if (createUserDto.getPhoneNumber() != null || createUserDto.getEmail() != null) {

                if (isPhoneNumberUnique(createUserDto.getPhoneNumber()) &&
                        isEmailUnique(createUserDto.getEmail()) &&
                        isLoginUnique(createUserDto.getUsername())) {

                    createUserDto.setPassword(passwordEncoder.encode(createUserDto.getPassword()));

                    HashSet<String> phoneNumbers = new HashSet<>();
                    phoneNumbers.add(createUserDto.getPhoneNumber());

                    HashSet<String> emails = new HashSet<>();
                    emails.add(createUserDto.getEmail());

                    UserEntity user = UserEntity.builder()
                            .username(createUserDto.getUsername())
                            .password(createUserDto.getPassword())
                            .phoneNumbers(phoneNumbers)
                            .emails(emails)
                            .build();

                    BankAccountEntity bankAccount = BankAccountEntity.builder()
                            .balance(createUserDto.getStartBalance())
                            .build();
                    bankAccount.setId(bankAccountRepo.save(bankAccount).getId());

                    user.setBankAccount(bankAccount);

                    userRepo.save(user);

                    return userMapper.toDto(user);

                } else {
                    throw new IllegalArgumentException("Phone number, email or username is not unique!");
                }

            } else {
                throw new IllegalArgumentException("Phone number or email are required!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String addPhoneNumber(String phoneNumberToAdd) throws NullPointerException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserEntity user = userRepo.findByUsername(authentication.getName());

        if (phoneNumberToAdd != null && isPhoneNumberUnique(phoneNumberToAdd)) {
            user.getPhoneNumbers().add(phoneNumberToAdd);
            userRepo.save(user);
        } else {
            throw new IllegalArgumentException(String.format("Number - %s is already used!", phoneNumberToAdd));
        }
        return String.format("Number - %s is added successfully!", phoneNumberToAdd);
    }

    @Override
    public String changePhoneNumber(String numberToChange, String newNumber) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserEntity user = userRepo.findByUsername(authentication.getName());

        if (numberToChange != null && (newNumber != null && isPhoneNumberUnique(newNumber))) {

            if (user.getPhoneNumbers().contains(numberToChange)) {
                user.getPhoneNumbers().remove(numberToChange);
                user.getPhoneNumbers().add(newNumber);
                userRepo.save(user);
            } else {
                throw new IllegalArgumentException("User does not have this number - " + numberToChange);
            }

        } else throw new IllegalArgumentException(String.format("Number %s can not be added to user", newNumber));

        return "Number successfully changed!";
    }

    @Override
    public String addEmail(String emailToAdd) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserEntity user = userRepo.findByUsername(authentication.getName());

        if (emailToAdd != null && isEmailUnique(emailToAdd)) {
            user.getEmails().add(emailToAdd);
            userRepo.save(user);
        } else {
            throw new IllegalArgumentException(String.format("Email - %s is already used!", emailToAdd));
        }
        return String.format("Email - %s is added successfully!", emailToAdd);
    }

    @Override
    public String changeEmail(String emailToChange, String newEmail) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserEntity user = userRepo.findByUsername(authentication.getName());

        if (emailToChange != null && (newEmail != null && isEmailUnique(newEmail))) {

            if (user.getPhoneNumbers().contains(emailToChange)) {
                user.getPhoneNumbers().remove(emailToChange);
                user.getPhoneNumbers().add(newEmail);
                userRepo.save(user);
            } else {
                throw new IllegalArgumentException("User does not have this email - " + emailToChange);
            }

        } else throw new IllegalArgumentException(String.format("Email %s can not be added to user!", newEmail));

        return "Email successfully changed!";
    }

    @Override
    public String deletePhoneNumber(String numberToDelete) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserEntity user = userRepo.findByUsername(authentication.getName());

        if (numberToDelete != null) {

            if (user.getPhoneNumbers().contains(numberToDelete) && user.getPhoneNumbers().size() > 1) {
                user.getPhoneNumbers().remove(numberToDelete);
                userRepo.save(user);
            } else {
                throw new IllegalArgumentException(String.format("Number %s can not be deleted!", numberToDelete));
            }

        } else {
            throw new IllegalArgumentException("Number is required!");
        }
        return String.format("Number %s has been deleted successfully!", numberToDelete);
    }

    @Override
    public String deleteEmail(String emailToDelete) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserEntity user = userRepo.findByUsername(authentication.getName());

        if (emailToDelete != null) {

            if (user.getEmails().contains(emailToDelete) && user.getEmails().size() > 1) {
                user.getEmails().remove(emailToDelete);
                userRepo.save(user);
            } else {
                throw new IllegalArgumentException(String.format("Email %s can not be deleted!", emailToDelete));
            }

        } else {
            throw new IllegalArgumentException("Email is required!");
        }
        return String.format("Email %s has been deleted successfully!", emailToDelete);
    }

    @Override
    public List<UserDto> findByFilters(Date dateOfBirth,
                                       String phoneNumber,
                                       String fullName,
                                       String email) {
        return userDao.findByFilters(dateOfBirth, phoneNumber, fullName, email);
    }

    private boolean isLoginUnique(String username) {
        UserEntity user = userRepo.findByUsername(username);
        return user == null;
    }

    private boolean isEmailUnique(String email) {
        return userRepo.findAll().stream()
                .noneMatch(user -> user.getEmails().contains(email));
    }

    private boolean isPhoneNumberUnique(String phoneNumber) {
        return userRepo.findAll().stream()
                .noneMatch(user -> user.getPhoneNumbers().contains(phoneNumber));
    }


}