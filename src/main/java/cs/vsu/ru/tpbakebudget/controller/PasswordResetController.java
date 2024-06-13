package cs.vsu.ru.tpbakebudget.controller;

import cs.vsu.ru.tpbakebudget.component.EmailComponent;
import cs.vsu.ru.tpbakebudget.dto.auth.ResetPasswordRequestDTO;
import cs.vsu.ru.tpbakebudget.model.PasswordReset;
import cs.vsu.ru.tpbakebudget.model.Users;
import cs.vsu.ru.tpbakebudget.service.impl.PasswordResetServiceImpl;
import cs.vsu.ru.tpbakebudget.service.impl.UsersServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    private final PasswordResetServiceImpl resetService;

    private final UsersServiceImpl usersService;

    private final EmailComponent emailComponent;

    @Autowired
    public PasswordResetController(PasswordResetServiceImpl resetService, UsersServiceImpl usersService, EmailComponent emailComponent) {
        this.resetService = resetService;
        this.usersService = usersService;
        this.emailComponent = emailComponent;
    }

    @PostMapping("/forgotPassword")
    @Operation(summary = "Forgot Password", description = "Initiate a password reset process for the user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset link has been sent to your email."),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "User not found"))),
            @ApiResponse(responseCode = "409", description = "Conflict - Reset token already created for this user",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Reset Token already created for this user"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) throws MessagingException, UnsupportedEncodingException {
        Users user = usersService.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        if (resetService.isResetRequestValid(user.getId())) {
            return new ResponseEntity<>("Reset Token already created for this user", HttpStatus.CONFLICT);
        } else {
            resetService.delete(user.getId());
            PasswordReset resetToken = resetService.createPasswordResetRequest(user.getId());
            emailComponent.sendPasswordResetEmail(email, resetToken.getToken());
            return ResponseEntity.ok("Password reset link has been sent to your email.");
        }
    }

    @GetMapping("/approveLink")
    @Operation(summary = "Approve Token", description = "Approve a password reset token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token Approved"),
            @ApiResponse(responseCode = "403", description = "Invalid or expired reset token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid or expired reset token"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> approveToken(@RequestParam("token") String resetToken) {
        PasswordReset reset = resetService.findByToken(resetToken);
        if (!resetService.isResetRequestValid(reset.getUser().getId())) {
            return new ResponseEntity<>("Invalid or expired reset token", HttpStatus.FORBIDDEN);
        }
        resetService.update(reset);
        return new ResponseEntity<>("Your request successfully approved, please turn back to the BakeBudget app", HttpStatus.OK);
    }

    @PostMapping("/resetPassword")
    @Operation(summary = "Reset Password", description = "Reset the password for a user using a reset token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password has been reset successfully."),
            @ApiResponse(responseCode = "403", description = "Invalid or expired reset token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid or expired reset token"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "User not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO) {
        Users user = usersService.findByEmail(resetPasswordRequestDTO.getEmail());
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        PasswordReset reset = resetService.findByUserId(user.getId());
        if (reset == null || !reset.isUsed()) {
            return new ResponseEntity<>("Invalid or expired reset token", HttpStatus.FORBIDDEN);
        }
        usersService.setNewPassword(user, resetPasswordRequestDTO.getNewPassword());
        resetService.delete(user.getId());
        return new ResponseEntity<>("Password has been reset successfully", HttpStatus.OK);
    }
}
