package my.code.effectivemobiletest.repositories;

import my.code.effectivemobiletest.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<UserEntity, Long> {
    UserEntity findByUsername(String username);
}