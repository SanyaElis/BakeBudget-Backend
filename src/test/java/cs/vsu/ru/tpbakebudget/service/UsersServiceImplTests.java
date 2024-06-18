package cs.vsu.ru.tpbakebudget.service;

import cs.vsu.ru.tpbakebudget.enums.Role;
import cs.vsu.ru.tpbakebudget.exception.NotFoundException;
import cs.vsu.ru.tpbakebudget.model.Users;
import cs.vsu.ru.tpbakebudget.repository.UsersRepository;
import cs.vsu.ru.tpbakebudget.service.impl.UsersServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UsersServiceImplTests {

    @Mock
    private UsersRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsersServiceImpl service;

    private Users user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new Users();
        user.setId(1L);
        user.setEmail("test@example.com");
        String rawPassword = "password";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);
        user.setRole(Role.ROLE_USER);
    }

    @Test
    public void testSaveNewUser() {
        when(repository.findByEmail(user.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(user.getPassword())).thenReturn(user.getPassword());
        when(repository.save(user)).thenReturn(user);

        Users savedUser = service.save(user);

        assertNotNull(savedUser);
        assertEquals(user.getEmail(), savedUser.getEmail());
        verify(repository, times(1)).findByEmail(user.getEmail());
        verify(passwordEncoder, times(1)).encode(user.getPassword());
        verify(repository, times(1)).save(user);
    }


    @Test
    public void testSaveUserWithExistingEmail() {
        when(repository.findByEmail(user.getEmail())).thenReturn(user);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.save(user));

        assertEquals("email already used", exception.getMessage());
        verify(repository, times(1)).findByEmail(user.getEmail());
        verify(passwordEncoder, times(0)).encode(user.getPassword());
        verify(repository, times(0)).save(user);
    }

    @Test
    public void testFindByEmail() {
        when(repository.findByEmail("test@example.com")).thenReturn(user);

        Users foundUser = service.findByEmail("test@example.com");

        assertNotNull(foundUser);
        assertEquals("test@example.com", foundUser.getEmail());
        verify(repository, times(1)).findByEmail("test@example.com");
    }

    @Test
    public void testFindById() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        Users foundUser = service.findById(1L);

        assertNotNull(foundUser);
        assertEquals(1L, foundUser.getId());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    public void testFindByIdNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findById(1L));
        verify(repository, times(1)).findById(1L);
    }

    @Test
    public void testSaveAll() {
        List<Users> usersList = Collections.singletonList(user);
        when(repository.saveAll(usersList)).thenReturn(usersList);

        List<Users> savedUsers = service.saveAll(usersList);

        assertNotNull(savedUsers);
        assertEquals(1, savedUsers.size());
        verify(repository, times(1)).saveAll(usersList);
    }

    @Test
    public void testFindAll() {
        when(repository.findAll()).thenReturn(Collections.singletonList(user));

        List<Users> users = service.findAll();

        assertNotNull(users);
        assertEquals(1, users.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    public void testUpdateUser() {
        when(repository.save(user)).thenReturn(user);

        Users updatedUser = service.update(user);

        assertNotNull(updatedUser);
        assertEquals(user.getEmail(), updatedUser.getEmail());
        verify(repository, times(1)).save(user);
    }

    @Test
    public void testDeleteUser() {
        service.delete(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    public void testSetNewPassword() {
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(repository.save(user)).thenReturn(user);

        Users updatedUser = service.setNewPassword(user, "newPassword");

        assertNotNull(updatedUser);
        assertEquals("encodedNewPassword", updatedUser.getPassword());
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(repository, times(1)).save(user);
    }

    @Test
    public void testUpdatePassword() {
        user.setPassword("encodedOldPassword");
        when(passwordEncoder.matches("oldPassword", user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(repository.save(user)).thenReturn(user);

        boolean result = service.updatePassword(user, "oldPassword", "newPassword");

        assertTrue(result);
        assertEquals("encodedNewPassword", user.getPassword());
        verify(passwordEncoder, times(1)).matches("oldPassword", "encodedOldPassword");
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(repository, times(1)).save(user);
    }

    @Test
    public void testUpdatePasswordWrongOldPassword() {
        when(passwordEncoder.matches("oldPassword", user.getPassword())).thenReturn(false);

        boolean result = service.updatePassword(user, "oldPassword", "newPassword");

        assertFalse(result);
        verify(passwordEncoder, times(1)).matches("oldPassword", user.getPassword());
        verify(passwordEncoder, times(0)).encode("newPassword");
        verify(repository, times(0)).save(user);
    }

    @Test
    public void testCreateGroupCode() {
        when(repository.save(user)).thenReturn(user);

        String groupCode = service.createGroupCode(user);

        assertNotNull(groupCode);
        assertEquals(36, groupCode.length());
        verify(repository, times(1)).save(user);
    }

    @Test
    public void testFindByRoleAndGroupCode() {
        when(repository.findByRoleAndGroupCode(Role.ROLE_USER, "groupCode")).thenReturn(user);

        Users foundUser = service.findByRoleAndGroupCode(Role.ROLE_USER, "groupCode");

        assertNotNull(foundUser);
        assertEquals(Role.ROLE_USER, foundUser.getRole());
        verify(repository, times(1)).findByRoleAndGroupCode(Role.ROLE_USER, "groupCode");
    }

    @Test
    public void testFindAllByGroupCode() {
        when(repository.findAllByGroupCode("groupCode")).thenReturn(Collections.singletonList(user));

        List<Users> users = service.findAllByGroupCode("groupCode");

        assertNotNull(users);
        assertEquals(1, users.size());
        verify(repository, times(1)).findAllByGroupCode("groupCode");
    }
}
