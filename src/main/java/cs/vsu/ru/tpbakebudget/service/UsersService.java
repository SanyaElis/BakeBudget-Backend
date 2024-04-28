package cs.vsu.ru.tpbakebudget.service;

import cs.vsu.ru.tpbakebudget.model.Users;

import java.util.List;

public interface UsersService {
    Users save(Users user);

    Users findByEmail(String email);

    Users findById(Long id);

    List<Users> saveAll(List<Users> usersList);

    List<Users> findAll();

    Users update(Long id, Users user);

    boolean updatePassword(Users user, String oldPassword, String newPassword);

    void delete(Long id);
}
