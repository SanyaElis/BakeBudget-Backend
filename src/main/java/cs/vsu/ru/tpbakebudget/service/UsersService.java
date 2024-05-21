package cs.vsu.ru.tpbakebudget.service;

import cs.vsu.ru.tpbakebudget.enums.Role;
import cs.vsu.ru.tpbakebudget.model.Users;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UsersService {
    Users save(Users user);

    Users findByEmail(String email);

    Users findById(Long id);

    List<Users> saveAll(List<Users> usersList);

    List<Users> findAll();

    Users update(Users user);

    boolean updatePassword(Users user, String oldPassword, String newPassword);

    String createGroupCode(Users user);

    Users findByRoleAndGroupCode(Role role, String groupCode);

    List<Users> findAllByGroupCode(String groupCode);

    void delete(Long id);

    Users setNewPassword(Users user, String newPassword);
}