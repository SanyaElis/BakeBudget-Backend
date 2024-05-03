package cs.vsu.ru.tpbakebudget.repository;

import cs.vsu.ru.tpbakebudget.enums.Role;
import cs.vsu.ru.tpbakebudget.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByEmail(String email);

    List<Users> findAllByGroupCode(String groupCode);

    Users findByRoleAndGroupCode(Role role, String groupCode);
}
