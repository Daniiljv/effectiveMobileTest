package my.code.effectivemobiletest.services.impl;

import com.expert.crmbackend.dto.UserDto;
import com.expert.crmbackend.entity.Role;
import com.expert.crmbackend.entity.User;
import com.expert.crmbackend.exception.UserNotFoundException;
import com.expert.crmbackend.mapper.UserMapper;
import com.expert.crmbackend.repo.UserRepo;
import com.expert.crmbackend.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        if (user.isBlocked()) {
            throw new LockedException("User is locked");
        }

        user.setLastAuthentication(LocalDateTime.now());
        userRepo.save(user);

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public UserDto save(UserDto userDto) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User user = userMapper.toEntity(userDto);
        userRepo.save(user);
        return userMapper.toDto(user);
    }

    public void block(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с Id " + userId + " не найден"));
        user.setBlocked(true);
        userRepo.save(user);
    }

    public void unblock(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с Id " + userId + " не найден"));
        user.setBlocked(false);
        userRepo.save(user);
    }

    @Override
    public UserDto getById(Long id) {
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("Пользователь с таким id: " + id + " не найден"));
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        List<User> users = userRepo.findAll();
        return userMapper.toDtoList(users);
    }

    @Override
    public UserDto update(UserDto userDto) {
        User user = userRepo.findById(userDto.getId()).orElseThrow(() -> new RuntimeException("Пользователь с таким id: " + userDto.getId() + " не найден"));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getSurname() != null) {
            user.setSurname(userDto.getSurname());
        }
        if (userDto.getRoles() != null) {
            for (Role role : userDto.getRoles()) {
                user.getRoles().add(role);
            }
        }
        userRepo.save(user);
        return userMapper.toDto(user);
    }

}