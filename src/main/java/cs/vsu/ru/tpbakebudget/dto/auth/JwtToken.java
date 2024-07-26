package cs.vsu.ru.tpbakebudget.dto.auth;

import cs.vsu.ru.tpbakebudget.enums.Role;
import lombok.Data;

@Data
public class JwtToken {
    private String token;
    private String type = "Bearer";
    private String refreshToken;
    private Long id;
    private String username;
    private String email;
    private Role role;

    public JwtToken(String accessToken, String refreshToken, Long id, String username, String email, Role role) {
        this.token = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }
}

