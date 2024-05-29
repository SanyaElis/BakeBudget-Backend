package cs.vsu.ru.tpbakebudget.service;

import cs.vsu.ru.tpbakebudget.model.PasswordReset;
import org.springframework.stereotype.Component;

@Component
public interface PasswordResetService {

    PasswordReset createPasswordResetRequest(Long userId);

    PasswordReset findByUserId(Long userId);

    public boolean isResetRequestValid(Long userId);

    public void delete(Long userId);

    public PasswordReset update(PasswordReset passwordReset);

    PasswordReset findByToken(String token);
}
