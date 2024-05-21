package cs.vsu.ru.tpbakebudget.controller;

import cs.vsu.ru.tpbakebudget.dto.auth.PasswordChangeRequestDTO;
import cs.vsu.ru.tpbakebudget.enums.Role;
import cs.vsu.ru.tpbakebudget.exception.NotFoundException;
import cs.vsu.ru.tpbakebudget.model.Users;
import cs.vsu.ru.tpbakebudget.service.impl.UsersServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class UsersController {

    private final UsersServiceImpl usersService;

    @Autowired
    public UsersController(UsersServiceImpl usersService) {
        this.usersService = usersService;
    }

    @PutMapping("/updatePassword")
    @Operation(summary = "Update Password", description = "Update user's password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Incorrect old password",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Incorrect old password"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Old password is incorrect"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> updatePassword(@Valid @RequestBody PasswordChangeRequestDTO updatePasswordRequest) {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (usersService.updatePassword(users, updatePasswordRequest.getOldPassword(), updatePasswordRequest.getNewPassword())) {
            return ResponseEntity.ok().body("Password updated successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect old password");
        }
    }

    @PutMapping("/createGroupCode")
    @PreAuthorize("hasRole('ROLE_ADVANCED_USER')")
    @Operation(summary = "Create group code", description = "Create user's group code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code created successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> createCode() {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String groupCode = usersService.createGroupCode(users);
        return new ResponseEntity<>(groupCode, HttpStatus.OK);
    }

    @GetMapping("/getGroupCode")
    @PreAuthorize("hasRole('ROLE_ADVANCED_USER')")
    @Operation(summary = "Get group code", description = "Get user's group code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code successfully found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> getIngredientById() {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String groupCode = users.getGroupCode();
        if (groupCode == null) {
            throw new NotFoundException("Code not found");
        }
        return new ResponseEntity<>(groupCode, HttpStatus.OK);
    }

    @PutMapping("/setGroupCode")
    @Operation(summary = "Set group code", description = "Set user's group code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code set successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Group code not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Group code not found"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "409", description = "Conflict - Creator already in self group",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Conflict: Creator already in self group"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> setCode(String groupCode) {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (usersService.findByRoleAndGroupCode(Role.ROLE_ADVANCED_USER, groupCode) == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Group code not found");
        }
        if(users.getRole() == Role.ROLE_ADVANCED_USER){
            return new ResponseEntity<>("Creator already in self group", HttpStatus.CONFLICT);
        }
        users.setGroupCode(groupCode);
        usersService.update(users);
        return new ResponseEntity<>(groupCode, HttpStatus.OK);
    }

    @PutMapping("/leaveGroup")
    @Operation(summary = "Leave group", description = "Leave user's group.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully leaved"),
            @ApiResponse(responseCode = "400", description = "Bad request - not in the group",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Not in the group"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> leaveGroup() {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (users.getGroupCode() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not in the group");
        }
        users.setGroupCode(null);
        usersService.update(users);
        return ResponseEntity.ok("Successfully leaved");
    }

    @PutMapping("/changeRole")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Change role", description = "Change user's role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role successfully changed"),
            @ApiResponse(responseCode = "400", description = "Bad request - already advanced user",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Already advanced user"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> changeRole() {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (users.getRole() == Role.ROLE_ADVANCED_USER) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Already advanced user");
        }
        users.setRole(Role.ROLE_ADVANCED_USER);
        usersService.update(users);
        return ResponseEntity.ok("Role successfully updated");
    }
}
