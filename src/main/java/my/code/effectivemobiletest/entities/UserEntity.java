package my.code.effectivemobiletest.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.FetchType.EAGER;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_generator")
    @SequenceGenerator(name = "user_seq_generator", sequenceName = "user_seq", allocationSize = 1)
    private Long id;

    @OneToOne
    private BankAccountEntity bankAccount;

    private HashSet<String> phoneNumbers = new HashSet<>();

    private HashSet<String> emails = new HashSet<>();

    private Date dateOfBirth;

    private String fullName;

    @Column(unique = true)
    private String username;

    private String password;

}
