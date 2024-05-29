package cs.vsu.ru.tpbakebudget.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class PasswordReset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private boolean isUsed;

    private LocalDateTime creationTime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

}
