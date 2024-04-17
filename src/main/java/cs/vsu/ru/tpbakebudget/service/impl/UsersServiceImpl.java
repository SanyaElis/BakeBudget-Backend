package cs.vsu.ru.tpbakebudget.service.impl;

import cs.vsu.ru.tpbakebudget.model.Users;
import cs.vsu.ru.tpbakebudget.repository.UsersRepository;
import cs.vsu.ru.tpbakebudget.security.enums.Role;
import cs.vsu.ru.tpbakebudget.service.UsersService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Component
public class UsersServiceImpl implements UsersService {

    private final UsersRepository repository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersServiceImpl(UsersRepository usersRepository) {
        this.repository = usersRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
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
        return repository.findById(id).orElse(null);
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
    public Users update(Long id, @NotNull Users newUser) {
        Users user = repository.findById(id).orElseThrow();
        user.setUsername(newUser.getUsername());
        user.setEmail(newUser.getEmail());
        return repository.save(user);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public void updatePassword(Long id, String oldPassword, String newPassword) {
        Users user = repository.findById(id).orElseThrow();
        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
        } else {
            throw new IllegalArgumentException("Old password is incorrect");
        }
    }
}
