package cs.vsu.ru.tpbakebudget.controller;

import cs.vsu.ru.tpbakebudget.component.EmailComponent;
import cs.vsu.ru.tpbakebudget.dto.auth.ResetPasswordRequestDTO;
import cs.vsu.ru.tpbakebudget.model.PasswordReset;
import cs.vsu.ru.tpbakebudget.model.Users;
import cs.vsu.ru.tpbakebudget.service.impl.PasswordResetServiceImpl;
import cs.vsu.ru.tpbakebudget.service.impl.UsersServiceImpl;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PasswordResetControllerTest {

    @Mock
    private PasswordResetServiceImpl resetService;

    @Mock
    private UsersServiceImpl usersService;

    @Mock
    private EmailComponent emailComponent;

    @InjectMocks
    private PasswordResetController passwordResetController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testForgotPassword_UserNotFound() throws MessagingException, UnsupportedEncodingException {
        when(usersService.findByEmail("test@example.com")).thenReturn(null);

        ResponseEntity<String> response = passwordResetController.forgotPassword("test@example.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    public void testForgotPassword_ResetTokenAlreadyCreated() throws MessagingException, UnsupportedEncodingException {
        Users user = new Users();
        user.setId(1L);

        when(usersService.findByEmail("test@example.com")).thenReturn(user);
        when(resetService.isResetRequestValid(1L)).thenReturn(true);

        ResponseEntity<String> response = passwordResetController.forgotPassword("test@example.com");

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Reset Token already created for this user", response.getBody());
    }

    @Test
    public void testForgotPassword_Success() throws MessagingException, UnsupportedEncodingException {
        Users user = new Users();
        user.setId(1L);
        PasswordReset resetToken = new PasswordReset();
        resetToken.setToken("resetToken");

        when(usersService.findByEmail("test@example.com")).thenReturn(user);
        when(resetService.isResetRequestValid(1L)).thenReturn(false);
        when(resetService.createPasswordResetRequest(1L)).thenReturn(resetToken);

        ResponseEntity<String> response = passwordResetController.forgotPassword("test@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password reset link has been sent to your email.", response.getBody());
        verify(emailComponent, times(1)).sendPasswordResetEmail("test@example.com", "resetToken");
    }

    @Test
    public void testApproveToken_InvalidToken() {
        PasswordReset reset = new PasswordReset();
        Users user = new Users();
        user.setId(1L);
        reset.setUser(user);

        when(resetService.findByToken("invalidToken")).thenReturn(reset);
        when(resetService.isResetRequestValid(1L)).thenReturn(false);

        ResponseEntity<?> response = passwordResetController.approveToken("invalidToken");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Invalid or expired reset token", response.getBody());
    }

    @Test
    public void testApproveToken_Success() {
        PasswordReset reset = new PasswordReset();
        Users user = new Users();
        user.setId(1L);
        reset.setUser(user);

        when(resetService.findByToken("validToken")).thenReturn(reset);
        when(resetService.isResetRequestValid(1L)).thenReturn(true);

        ResponseEntity<?> response = passwordResetController.approveToken("validToken");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Your request successfully approved, please turn back to the BakeBudget app", response.getBody());
    }

    @Test
    public void testResetPassword_UserNotFound() {
        ResetPasswordRequestDTO request = new ResetPasswordRequestDTO();
        request.setEmail("test@example.com");
        request.setNewPassword("newPassword");

        when(usersService.findByEmail("test@example.com")).thenReturn(null);

        ResponseEntity<?> response = passwordResetController.resetPassword(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    public void testResetPassword_InvalidToken() {
        Users user = new Users();
        user.setId(1L);
        ResetPasswordRequestDTO request = new ResetPasswordRequestDTO();
        request.setEmail("test@example.com");
        request.setNewPassword("newPassword");

        when(usersService.findByEmail("test@example.com")).thenReturn(user);
        when(resetService.findByUserId(1L)).thenReturn(null);

        ResponseEntity<?> response = passwordResetController.resetPassword(request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Invalid or expired reset token", response.getBody());
    }

    @Test
    public void testResetPassword_Success() {
        Users user = new Users();
        user.setId(1L);
        ResetPasswordRequestDTO request = new ResetPasswordRequestDTO();
        request.setEmail("test@example.com");
        request.setNewPassword("newPassword");

        PasswordReset reset = new PasswordReset();
        reset.setUsed(true);

        when(usersService.findByEmail("test@example.com")).thenReturn(user);
        when(resetService.findByUserId(1L)).thenReturn(reset);

        ResponseEntity<?> response = passwordResetController.resetPassword(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password has been reset successfully", response.getBody());
        verify(usersService, times(1)).setNewPassword(user, "newPassword");
        verify(resetService, times(1)).delete(1L);
    }
}
