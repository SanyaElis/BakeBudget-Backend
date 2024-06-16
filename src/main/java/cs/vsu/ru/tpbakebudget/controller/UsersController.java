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

import java.util.List;

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
            @ApiResponse(responseCode = "409", description = "Conflict - Already in group",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Conflict: Already in group"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> createCode() {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(users.getGroupCode() != null){
            return new ResponseEntity<>("Already in group", HttpStatus.CONFLICT);
        }
        String groupCode = usersService.createGroupCode(users);
        return new ResponseEntity<>(groupCode, HttpStatus.OK);
    }

    @GetMapping("/getGroupCode")
    @Operation(summary = "Get group code", description = "Get user's group code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code successfully found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Not Found - Group code not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Group code not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> getGroupCode() {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String groupCode = users.getGroupCode();
        if (groupCode == null) {
            return new ResponseEntity<>("Group code not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(groupCode, HttpStatus.OK);
    }

    @PutMapping("/setGroupCode")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Set group code", description = "Set user's group code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code set successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Not Found - Group code not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Group code not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> setCode(String groupCode) {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (usersService.findByRoleAndGroupCode(Role.ROLE_ADVANCED_USER, groupCode) == null) {
            return new ResponseEntity<>("Group code not found", HttpStatus.NOT_FOUND);
        }
        users.setGroupCode(groupCode);
        usersService.update(users);
        return new ResponseEntity<>(groupCode, HttpStatus.OK);
    }

    @PutMapping("/leaveGroup")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Leave group", description = "Leave user's group.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully leaved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Not Found - Group code not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Group code not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> leaveGroup() {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (users.getGroupCode() == null) {
            return new ResponseEntity<>("Group code not found", HttpStatus.NOT_FOUND);
        }
        users.setGroupCode(null);
        usersService.update(users);
        return ResponseEntity.ok("Successfully leaved");
    }

    @DeleteMapping("/deleteGroup")
    @PreAuthorize("hasRole('ROLE_ADVANCED_USER')")
    @Operation(summary = "Delete Group", description = "Delete a group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Group not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Group not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> deleteGroup() {
        Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String groupCode = user.getGroupCode();
        if (groupCode == null) {
            return new ResponseEntity<>("Group code not found", HttpStatus.NOT_FOUND);
        }
        List<Users> usersInGroup = usersService.findAllByGroupCode(groupCode);
        for (Users currentUser : usersInGroup) {
            currentUser.setGroupCode(null);
            usersService.update(currentUser);
        }

        return ResponseEntity.ok("Group deleted successfully");
    }

    @PutMapping("/changeRole")
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
        users.setGroupCode(null);
        usersService.update(users);
        return ResponseEntity.ok("Role successfully updated");
    }
}
