package my.code.effectivemobiletest.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.code.effectivemobiletest.dao.UserDao;
import my.code.effectivemobiletest.dtos.CreateUserDto;
import my.code.effectivemobiletest.dtos.UserDto;
import my.code.effectivemobiletest.entities.BankAccountEntity;
import my.code.effectivemobiletest.entities.UserEntity;
import my.code.effectivemobiletest.exceptions.InsufficientFundsException;
import my.code.effectivemobiletest.exceptions.SameUserTransactionException;
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

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;


@Service
@Slf4j
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
        log.info(String.format("Started UserServiceImpl create(%s)", createUserDto));
        try {
            if (createUserDto.getPhoneNumber() != null || createUserDto.getEmail() != null) {

                if (isPhoneNumberUnique(createUserDto.getPhoneNumber()) &&
                        isEmailUnique(createUserDto.getEmail()) &&
                        isLoginUnique(createUserDto.getUsername())) {

                    if (createUserDto.getStartBalance().doubleValue() > 0) {

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
                                .startBalance(createUserDto.getStartBalance())
                                .currentBalance(createUserDto.getStartBalance())
                                .build();
                        bankAccount.setId(bankAccountRepo.save(bankAccount).getId());

                        user.setBankAccount(bankAccount);

                        userRepo.save(user);

                        log.info(String.format("Finished UserServiceImpl created user - %s", userMapper.toDto(user)));
                        return userMapper.toDto(user);
                    } else {
                        log.warn("User can not have negative balance!");
                        throw new IllegalArgumentException("User can not have negative balance!");
                    }

                } else {
                    log.warn("Phone number, email or username is not unique!");
                    throw new IllegalArgumentException("Phone number, email or username is not unique!");
                }

            } else {
                log.warn("Phone number or email are required!");
                throw new IllegalArgumentException("Phone number or email are required!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String addPhoneNumber(String phoneNumberToAdd) throws NullPointerException {
        log.info(String.format("Started UserServiceImpl addPhoneNumber(%s)", phoneNumberToAdd));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserEntity user = userRepo.findByUsername(authentication.getName());

        if (phoneNumberToAdd != null && isPhoneNumberUnique(phoneNumberToAdd)) {
            user.getPhoneNumbers().add(phoneNumberToAdd);
            userRepo.save(user);
        } else {
            log.warn(String.format("Number - %s is already used!", phoneNumberToAdd));
            throw new IllegalArgumentException(String.format("Number - %s is already used!", phoneNumberToAdd));
        }
        log.info(String.format("Finished UserServiceImpl addPhoneNumber() - %s is added successfully!", phoneNumberToAdd));
        return String.format("Number - %s is added successfully!", phoneNumberToAdd);
    }

    @Override
    public String changePhoneNumber(String numberForChange, String newNumber) {
        log.info(String.format("Started UserServiceImpl changePhoneNumber(from - %s, to - %s)",
                numberForChange, newNumber));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserEntity user = userRepo.findByUsername(authentication.getName());

        if (numberForChange != null && (newNumber != null && isPhoneNumberUnique(newNumber))) {

            if (user.getPhoneNumbers().contains(numberForChange)) {
                user.getPhoneNumbers().remove(numberForChange);
                user.getPhoneNumbers().add(newNumber);
                userRepo.save(user);
            } else {
                log.warn("User does not have this number - " + numberForChange);
                throw new IllegalArgumentException("User does not have this number - " + numberForChange);
            }

            log.warn(String.format("Number %s can not be changed!", numberForChange));
        } else throw new IllegalArgumentException(String.format("Number %s can not be changed!", numberForChange));

        log.info(String.format("Finished UserServiceImpl changePhoneNumber(from - %s, to - %s)",
                numberForChange, newNumber));
        return "Number successfully changed!";
    }

    @Override
    public String addEmail(String emailToAdd) {
        log.info(String.format("Started UserServiceImpl addEmail(%s)", emailToAdd));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserEntity user = userRepo.findByUsername(authentication.getName());

        if (emailToAdd != null && isEmailUnique(emailToAdd)) {
            user.getEmails().add(emailToAdd);
            userRepo.save(user);
        } else {
            log.warn(String.format("Email - %s is already used!", emailToAdd));
            throw new IllegalArgumentException(String.format("Email - %s is already used!", emailToAdd));
        }
        log.info(String.format("Finished UserServiceImpl addEmail() - %s is added successfully!", emailToAdd));
        return String.format("Email - %s is added successfully!", emailToAdd);
    }

    @Override
    public String changeEmail(String emailForChange, String newEmail) {
        log.info(String.format("Started UserServiceImpl changeEmail(from - %s, to - %s)",
                emailForChange, newEmail));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserEntity user = userRepo.findByUsername(authentication.getName());

        if (emailForChange != null && (newEmail != null && isEmailUnique(newEmail))) {

            if (user.getEmails().contains(emailForChange)) {
                user.getEmails().remove(emailForChange);
                user.getEmails().add(newEmail);
                userRepo.save(user);
            } else {
                log.warn("User does not have this email - " + emailForChange);
                throw new IllegalArgumentException("User does not have this email - " + emailForChange);
            }
            log.warn(String.format("Email %s can not be changed!", emailForChange));
        } else throw new IllegalArgumentException(String.format("Email %s can not be changed!", emailForChange));

        log.info(String.format("Finished UserServiceImpl changedEmail() new email - %s", newEmail));
        return "Email successfully changed!";
    }

    @Override
    public String deletePhoneNumber(String numberToDelete) {
        log.info(String.format("Started UserServiceImpl deletePhoneNumber(%s)", numberToDelete));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserEntity user = userRepo.findByUsername(authentication.getName());

        if (numberToDelete != null) {

            if (user.getPhoneNumbers().contains(numberToDelete) && user.getPhoneNumbers().size() > 1) {
                user.getPhoneNumbers().remove(numberToDelete);
                userRepo.save(user);
            } else {
                log.warn(String.format("Number %s can not be deleted!", numberToDelete));
                throw new IllegalArgumentException(String.format("Number %s can not be deleted!", numberToDelete));
            }

        } else {
            log.warn("Number is required!");
            throw new IllegalArgumentException("Number is required!");
        }
        log.info(String.format("Finished UserServiceImpl deletePhoneNumber(). Deleted - %s", numberToDelete));
        return String.format("Number %s has been deleted successfully!", numberToDelete);
    }

    @Override
    public String deleteEmail(String emailToDelete) {
        log.info(String.format("Started UserServiceImpl deleteEmail(%s)", emailToDelete));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserEntity user = userRepo.findByUsername(authentication.getName());

        if (emailToDelete != null) {

            if (user.getEmails().contains(emailToDelete) && user.getEmails().size() > 1) {
                user.getEmails().remove(emailToDelete);
                userRepo.save(user);
            } else {
                log.warn(String.format("Email %s can not be deleted!", emailToDelete));
                throw new IllegalArgumentException(String.format("Email %s can not be deleted!", emailToDelete));
            }

        } else {
            log.warn("Email is required!");
            throw new IllegalArgumentException("Email is required!");
        }
        log.info(String.format("Finished UserServiceImpl deleteEmail(). Deleted - %s", emailToDelete));
        return String.format("Email %s has been deleted successfully!", emailToDelete);
    }

    @Override
    public List<UserDto> findByFilters(Date dateOfBirth,
                                       String phoneNumber,
                                       String fullName,
                                       String email) {
        log.info(String.format("Started UserServiceImpl findByFilters(%s, %s, %s, %s)",
                dateOfBirth, phoneNumber, fullName, email));

        List<UserDto> result = userDao.findByFilters(dateOfBirth, phoneNumber, fullName, email);

        log.info(String.format("Finished UserServiceImpl findByFilters(). Result - %s",
                result));

        return result;
    }

    @Transactional
    @Override
    public String transaction(Long transferToUserId, BigDecimal amountToTransfer) {
        log.info(String.format("Started UserServiceImpl transaction(%s, %s)", transferToUserId, amountToTransfer));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserEntity userFrom = userRepo.findByUsername(authentication.getName());
        UserEntity userTo = userRepo.findById(transferToUserId)
                .orElseThrow(() -> {
                    log.warn("User with id " + transferToUserId + " is not found!");
                    return new NullPointerException("User with id " + transferToUserId + " is not found!");
                });

        BankAccountEntity bankAccountFrom = userFrom.getBankAccount();
        BankAccountEntity bankAccountTo = userTo.getBankAccount();

        if (userFrom.equals(userTo)) {
            log.warn("User cannot transfer money to their own account!");
            throw new SameUserTransactionException("User cannot transfer money to their own account!");
        }

        if (amountToTransfer.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Amount to transfer must be greater than zero!");
            throw new IllegalArgumentException("Amount to transfer must be greater than zero!");
        }

        if (bankAccountFrom.getCurrentBalance().compareTo(amountToTransfer) < 0) {
            log.warn("User doesn't have enough money for transaction! " +
                    "Please top up your balance.");
            throw new InsufficientFundsException("User doesn't have enough money for transaction! " +
                    "Please top up your balance.");
        }

        bankAccountFrom.setCurrentBalance(bankAccountFrom.getCurrentBalance().subtract(amountToTransfer));
        bankAccountTo.setCurrentBalance(bankAccountTo.getCurrentBalance().add(amountToTransfer));


        bankAccountRepo.save(bankAccountFrom);
        bankAccountRepo.save(bankAccountTo);

        return "Transaction has gone successfully!";
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