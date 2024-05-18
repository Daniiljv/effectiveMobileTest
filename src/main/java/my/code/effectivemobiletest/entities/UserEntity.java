package my.code.effectivemobiletest.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.FetchType.EAGER;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_generator")
    @SequenceGenerator(name = "user_seq_generator", sequenceName = "user_seq", allocationSize = 1)
    private Long id;
    private String name;
    private String surname;
    private String username;
    private String password;
    private boolean blocked;
    @ManyToMany(fetch = EAGER)
    private Set<Role> roles = new HashSet<>();

    private LocalDateTime lastAuthentication;
    private String position;
    private String phoneNumber;
}