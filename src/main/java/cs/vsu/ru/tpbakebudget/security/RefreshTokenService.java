package cs.vsu.ru.tpbakebudget.security;

import cs.vsu.ru.tpbakebudget.exception.TokenRefreshException;
import cs.vsu.ru.tpbakebudget.model.RefreshToken;
import cs.vsu.ru.tpbakebudget.repository.RefreshTokenRepository;
import cs.vsu.ru.tpbakebudget.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UsersService usersService;
    @Value("${app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UsersService usersService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.usersService = usersService;
    }

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken(usersService.findById(userId), UUID.randomUUID().toString(), Instant.now().plusMillis(refreshTokenDurationMs));
        return refreshTokenRepository.save(refreshToken);
    }

    public void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(usersService.findById(userId));
    }
}