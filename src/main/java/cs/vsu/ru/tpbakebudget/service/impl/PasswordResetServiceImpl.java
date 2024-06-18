package cs.vsu.ru.tpbakebudget.service.impl;

import cs.vsu.ru.tpbakebudget.model.PasswordReset;
import cs.vsu.ru.tpbakebudget.repository.PasswordResetRepository;
import cs.vsu.ru.tpbakebudget.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetRepository resetRequestRepository;

    private final UsersServiceImpl usersService;

    @Autowired
    public PasswordResetServiceImpl(PasswordResetRepository resetRequestRepository, UsersServiceImpl usersService) {
        this.resetRequestRepository = resetRequestRepository;
        this.usersService = usersService;
    }

    @Override
    public PasswordReset findByUserId(Long userId){
        return resetRequestRepository.findByUserId(userId);
    }

    @Override
    public PasswordReset createPasswordResetRequest(Long userId) {
        PasswordReset resetRequest = new PasswordReset();
        resetRequest.setUser(usersService.findById(userId));
        resetRequest.setCreationTime(LocalDateTime.now());
        resetRequest.setToken(UUID.randomUUID().toString());
        resetRequest.setUsed(false);
        return resetRequestRepository.save(resetRequest);
    }

    @Override
    public boolean isResetRequestValid(Long userId) {
        PasswordReset resetRequest = resetRequestRepository.findByUserId(userId);
        return resetRequest != null && !resetRequest.isUsed() && resetRequest.getCreationTime().isAfter(LocalDateTime.now().minusMinutes(30));
    }

    @Override
    public void delete(Long userId) {
        PasswordReset resetRequest = resetRequestRepository.findByUserId(userId);
        if (resetRequest != null) {
            resetRequestRepository.delete(resetRequest);
        }
    }

    @Override
    public PasswordReset update(PasswordReset passwordReset){
        passwordReset.setUsed(true);
        return resetRequestRepository.save(passwordReset);
    }

    @Override
    public PasswordReset findByToken(String token){
        return resetRequestRepository.findByToken(token);
    }
}