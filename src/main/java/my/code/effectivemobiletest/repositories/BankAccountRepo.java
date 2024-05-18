package my.code.effectivemobiletest.repositories;

import my.code.effectivemobiletest.entities.BankAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepo extends JpaRepository<BankAccountEntity, Long> {
}
