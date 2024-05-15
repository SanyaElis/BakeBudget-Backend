package cs.vsu.ru.tpbakebudget.service.impl;

import cs.vsu.ru.tpbakebudget.enums.Role;
import cs.vsu.ru.tpbakebudget.exception.NotFoundException;
import cs.vsu.ru.tpbakebudget.model.Users;
import cs.vsu.ru.tpbakebudget.repository.UsersRepository;
import cs.vsu.ru.tpbakebudget.service.UsersService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Component
public class UsersServiceImpl implements UsersService {

    private final UsersRepository repository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersServiceImpl(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.repository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Users save(Users user) {
        if (repository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("email already used");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_USER);
        return repository.save(user);
    }

    @Override
    public Users findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public Users findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }


    @Override
    public List<Users> saveAll(List<Users> users) {
        return repository.saveAll(users);
    }

    @Override
    public List<Users> findAll() {
        return repository.findAll();
    }

    @Override
    public Users update(@NotNull Users updatedUser) {
        return repository.save(updatedUser);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean updatePassword(Users user, String oldPassword, String newPassword) {
        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            repository.save(user);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String createGroupCode(Users user) {
        String groupCode = UUID.randomUUID().toString();
        user.setGroupCode(groupCode);
        repository.save(user);
        return groupCode;
    }

    @Override
    public Users findByRoleAndGroupCode(Role role, String groupCode) {
        return repository.findByRoleAndGroupCode(role, groupCode);
    }

    @Override
    public List<Users> findAllByGroupCode(String groupCode) {
        return repository.findAllByGroupCode(groupCode);
    }
}
