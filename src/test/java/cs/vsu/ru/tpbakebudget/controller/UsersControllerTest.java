package cs.vsu.ru.tpbakebudget.controller;

import cs.vsu.ru.tpbakebudget.dto.auth.PasswordChangeRequestDTO;
import cs.vsu.ru.tpbakebudget.enums.Role;
import cs.vsu.ru.tpbakebudget.model.Users;
import cs.vsu.ru.tpbakebudget.service.impl.UsersServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UsersControllerTest {

    @Mock
    private UsersServiceImpl usersService;

    private UsersController usersController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usersController = new UsersController(usersService);

        Users mockUser = new Users();
        mockUser.setId(1L);
        mockUser.setRole(Role.ROLE_USER);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testUpdatePassword_Success() {
        PasswordChangeRequestDTO requestDTO = new PasswordChangeRequestDTO();
        requestDTO.setOldPassword("oldPass");
        requestDTO.setNewPassword("newPass");

        when(usersService.updatePassword(any(Users.class), anyString(), anyString())).thenReturn(true);

        ResponseEntity<?> response = usersController.updatePassword(requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password updated successfully!", response.getBody());
    }

    @Test
    void testUpdatePassword_IncorrectOldPassword() {
        PasswordChangeRequestDTO requestDTO = new PasswordChangeRequestDTO();
        requestDTO.setOldPassword("oldPass");
        requestDTO.setNewPassword("newPass");

        when(usersService.updatePassword(any(Users.class), anyString(), anyString())).thenReturn(false);

        ResponseEntity<?> response = usersController.updatePassword(requestDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Incorrect old password", response.getBody());
    }

    @Test
    void testCreateCode_Success() {
        Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setGroupCode(null);

        when(usersService.createGroupCode(any(Users.class))).thenReturn("groupCode123");

        ResponseEntity<?> response = usersController.createCode();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("groupCode123", response.getBody());
    }

    @Test
    void testCreateCode_AlreadyInGroup() {
        Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setGroupCode("existingCode");

        ResponseEntity<?> response = usersController.createCode();

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Already in group", response.getBody());
    }

    @Test
    void testGetGroupCode_Success() {
        Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setGroupCode("groupCode123");

        ResponseEntity<?> response = usersController.getGroupCode();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("groupCode123", response.getBody());
    }

    @Test
    void testGetGroupCode_NotFound() {
        Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setGroupCode(null);

        ResponseEntity<?> response = usersController.getGroupCode();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Group code not found", response.getBody());
    }

    @Test
    void testSetCode_Success() {
        Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setGroupCode(null);

        when(usersService.findByRoleAndGroupCode(any(Role.class), anyString())).thenReturn(new Users());

        ResponseEntity<?> response = usersController.setCode("groupCode123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("groupCode123", response.getBody());
    }

    @Test
    void testSetCode_NotFound() {
        Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setGroupCode(null);

        when(usersService.findByRoleAndGroupCode(any(Role.class), anyString())).thenReturn(null);

        ResponseEntity<?> response = usersController.setCode("groupCode123");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Group code not found", response.getBody());
    }

    @Test
    void testLeaveGroup_Success() {
        Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setGroupCode("groupCode123");

        ResponseEntity<?> response = usersController.leaveGroup();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully leaved", response.getBody());
        verify(usersService, times(1)).update(any(Users.class));
    }

    @Test
    void testLeaveGroup_NotFound() {
        Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setGroupCode(null);

        ResponseEntity<?> response = usersController.leaveGroup();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Group code not found", response.getBody());
    }

    @Test
    void testDeleteGroup_Success() {
        Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setGroupCode("groupCode123");

        List<Users> usersInGroup = List.of(user);

        when(usersService.findAllByGroupCode(anyString())).thenReturn(usersInGroup);

        ResponseEntity<?> response = usersController.deleteGroup();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Group deleted successfully", response.getBody());
        verify(usersService, times(1)).update(any(Users.class));
    }

    @Test
    void testDeleteGroup_NotFound() {
        Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setGroupCode(null);

        ResponseEntity<?> response = usersController.deleteGroup();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Group code not found", response.getBody());
    }

    @Test
    void testChangeRole_Success() {
        Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setRole(Role.ROLE_USER);

        ResponseEntity<?> response = usersController.changeRole();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Role successfully updated", response.getBody());
        verify(usersService, times(1)).update(any(Users.class));
    }

    @Test
    void testChangeRole_AlreadyAdvancedUser() {
        Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setRole(Role.ROLE_ADVANCED_USER);

        ResponseEntity<?> response = usersController.changeRole();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Already advanced user", response.getBody());
    }
}
