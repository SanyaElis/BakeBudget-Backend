package cs.vsu.ru.tpbakebudget.controller;

import cs.vsu.ru.tpbakebudget.dto.auth.JwtToken;
import cs.vsu.ru.tpbakebudget.dto.auth.SignInRequest;
import cs.vsu.ru.tpbakebudget.dto.auth.SignUpRequest;
import cs.vsu.ru.tpbakebudget.enums.Role;
import cs.vsu.ru.tpbakebudget.exception.UserAlreadyExistsException;
import cs.vsu.ru.tpbakebudget.model.Users;
import cs.vsu.ru.tpbakebudget.security.jwt.JwtUtils;
import cs.vsu.ru.tpbakebudget.service.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthControllerTests {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsersService usersService;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAuthenticateUser() {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("test@example.com");
        signInRequest.setPassword("password");
        Users user = new Users("TestUser", "test@example.com", "password");
        user.setId(1L);
        user.setRole(Role.ROLE_USER);
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("token");
        ResponseEntity<?> response = authController.authenticateUser(signInRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("token", ((JwtToken) response.getBody()).getToken());
    }

    @Test
    public void testRegisterUser_Success() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("TestUser");
        signUpRequest.setEmail("test@example.com");
        signUpRequest.setPassword("password");
        Users user = new Users("TestUser", "test@example.com", "password");
        when(usersService.findByEmail("test@example.com")).thenReturn(null);
        ResponseEntity<?> response = authController.registerUser(signUpRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully!", response.getBody());
    }

    @Test
    public void testRegisterUser_UserAlreadyExists() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("TestUser");
        signUpRequest.setEmail("test@example.com");
        signUpRequest.setPassword("password");
        when(usersService.findByEmail("test@example.com")).thenReturn(new Users());
        assertThrows(UserAlreadyExistsException.class, () -> authController.registerUser(signUpRequest));
    }
}
