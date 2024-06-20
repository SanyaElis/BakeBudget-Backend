package cs.vsu.ru.tpbakebudget.controller;

import cs.vsu.ru.tpbakebudget.dto.auth.JwtToken;
import cs.vsu.ru.tpbakebudget.dto.auth.SignInRequest;
import cs.vsu.ru.tpbakebudget.dto.auth.SignUpRequest;
import cs.vsu.ru.tpbakebudget.enums.Role;
import cs.vsu.ru.tpbakebudget.exception.UserAlreadyExistsException;
import cs.vsu.ru.tpbakebudget.model.Users;
import cs.vsu.ru.tpbakebudget.security.jwt.JwtUtils;
import cs.vsu.ru.tpbakebudget.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;

    private final UsersService usersService;

    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, UsersService usersService, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.usersService = usersService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/signin")
    @Operation(summary = "Sign In", description = "Authenticate user with email and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SignInRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);

        Users user = (Users) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtToken(jwt,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()));
    }

    @PostMapping("/signup")
    @Operation(summary = "Sign Up", description = "Register a new user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "409", description = "Conflict - User with email already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "User with email example@example.com already exists"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (usersService.findByEmail(signUpRequest.getEmail()) != null) {
            return new ResponseEntity<>("User with email " + signUpRequest.getEmail() + " already exists", HttpStatus.CONFLICT);
        } else {
            Users user = new Users(signUpRequest.getUsername(), signUpRequest.getEmail(), signUpRequest.getPassword());
            usersService.save(user);
            return ResponseEntity.ok("User registered successfully!");
        }
    }
}
