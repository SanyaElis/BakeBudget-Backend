package cs.vsu.ru.tpbakebudget.controller;

import cs.vsu.ru.tpbakebudget.component.ReportCalculator;
import cs.vsu.ru.tpbakebudget.dto.request.orders.ReportRequestDTO;
import cs.vsu.ru.tpbakebudget.dto.responce.orders.IncomeResponseDTO;
import cs.vsu.ru.tpbakebudget.model.Users;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/report")
@SecurityRequirement(name = "bearerAuth")
public class ReportsController {

    private final ReportCalculator calculator;

    @Autowired
    public ReportsController(ReportCalculator calculator) {
        this.calculator = calculator;
    }

    @PostMapping("/calculateByOrderSelf")
    @Operation(summary = "Calculate report by order for user self", description = "Create a new report calculation by order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calculated successfully"),
            @ApiResponse(responseCode = "204", description = "No content",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "No content: No data found for the given criteria"))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid input: Please provide valid data"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> calculationByOrder(@Valid @RequestBody ReportRequestDTO reportRequestDTO) {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Integer> report = calculator.calculateByOrders(users.getId(),
                reportRequestDTO.getStartCreatedAt(), reportRequestDTO.getEndCreatedAt(),
                reportRequestDTO.getStartFinishedAt(), reportRequestDTO.getEndFinishedAt());

        if (report.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @PostMapping("/calculateByIncomeSelf")
    @Operation(summary = "Calculate report by income for user self", description = "Create a new report calculation by order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calculated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid input: Please provide valid data"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<IncomeResponseDTO> calculationByIncome(@Valid @RequestBody ReportRequestDTO reportRequestDTO) {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        IncomeResponseDTO incomeResponseDTO = calculator.calculateByIncome(users.getId(),
                reportRequestDTO.getStartCreatedAt(), reportRequestDTO.getEndCreatedAt(),
                reportRequestDTO.getStartFinishedAt(), reportRequestDTO.getEndFinishedAt());

        return new ResponseEntity<>(incomeResponseDTO, HttpStatus.OK);
    }

    @PostMapping("/calculateByOrderGroup")
    @PreAuthorize("hasRole('ROLE_ADVANCED_USER')")
    @Operation(summary = "Calculate report by order for advanced user and group", description = "Create a new report calculation by order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calculated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid input: Please provide valid data"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<Map<String, Integer>> calculationByOrderGroup(@Valid @RequestBody ReportRequestDTO reportForGroupDTO) {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<String, Integer> report = calculator.calculateByOrdersForGroup(users.getGroupCode(),
                reportForGroupDTO.getStartCreatedAt(), reportForGroupDTO.getEndCreatedAt(),
                reportForGroupDTO.getStartFinishedAt(), reportForGroupDTO.getEndFinishedAt());

        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @PostMapping("/calculateByIncomeGroup")
    @PreAuthorize("hasRole('ROLE_ADVANCED_USER')")
    @Operation(summary = "Calculate report by income for advanced user and group", description = "Create a new report calculation by order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calculated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid input: Please provide valid data"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<IncomeResponseDTO> calculationByIncomeGroup(@Valid @RequestBody ReportRequestDTO reportForGroupDTO) {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        IncomeResponseDTO incomeResponse = calculator.calculateByIncomeForGroup(users.getGroupCode(),
                reportForGroupDTO.getStartCreatedAt(), reportForGroupDTO.getEndCreatedAt(),
                reportForGroupDTO.getStartFinishedAt(), reportForGroupDTO.getEndFinishedAt());

        return new ResponseEntity<>(incomeResponse, HttpStatus.OK);
    }
}
