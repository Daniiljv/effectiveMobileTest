package my.code.effectivemobiletest.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@Table(name = "bank_accounts")
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_accounts_seq_generator")
    @SequenceGenerator(name = "bank_accounts_seq_generator", sequenceName = "bank_accounts_seq", allocationSize = 1)
    private Long id;

    private BigDecimal balance;
}
