package my.code.effectivemobiletest.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUserDto {
    private Long id;
    private String fullName;
    private Map<String, String> tokens;
}
