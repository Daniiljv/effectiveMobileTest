package my.code.effectivemobiletest.repositories;

import com.expert.crmbackend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Role findByNameIgnoreCase(String name);

    boolean existsByName(String name);

}