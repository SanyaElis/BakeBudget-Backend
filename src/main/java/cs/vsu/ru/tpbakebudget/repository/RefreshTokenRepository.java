package cs.vsu.ru.tpbakebudget.repository;

import cs.vsu.ru.tpbakebudget.model.RefreshToken;
import cs.vsu.ru.tpbakebudget.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByToken(String token);

    int deleteByUser(Users user);
}
