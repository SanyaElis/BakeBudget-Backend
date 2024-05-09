package cs.vsu.ru.tpbakebudget.controller;

import cs.vsu.ru.tpbakebudget.dto.request.IngredientsRequestDTO;
import cs.vsu.ru.tpbakebudget.dto.responce.IngredientsResponseDTO;
import cs.vsu.ru.tpbakebudget.mapper.IngredientsMapper;
import cs.vsu.ru.tpbakebudget.model.Ingredients;
import cs.vsu.ru.tpbakebudget.model.Users;
import cs.vsu.ru.tpbakebudget.service.impl.IngredientsServiceImpl;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@SecurityRequirement(name = "bearerAuth")
public class IngredientsController {

    private final IngredientsServiceImpl ingredientsService;

    private final IngredientsMapper mapper;

    @Autowired
    public IngredientsController(IngredientsServiceImpl ingredientsService, IngredientsMapper mapper) {
        this.ingredientsService = ingredientsService;
        this.mapper = mapper;
    }

    @PostMapping("/create")
    @Operation(summary = "Create Ingredient", description = "Create a new ingredient.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ingredient created successfully"),
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
    public ResponseEntity<IngredientsResponseDTO> createIngredient(@Valid @RequestBody IngredientsRequestDTO ingredientRequest) {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Ingredients ingredient = mapper.toEntity(ingredientRequest, users);
        Ingredients createdIngredient = ingredientsService.save(ingredient);

        return new ResponseEntity<>(mapper.toDto(createdIngredient), HttpStatus.CREATED);
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Get Ingredient by ID", description = "Get an ingredient by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingredient found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Ingredient not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Ingredient not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<IngredientsResponseDTO> getIngredientById(@PathVariable Long id) {
        Ingredients ingredient = ingredientsService.findById(id);
        return ResponseEntity.ok(mapper.toDto(ingredient));
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update Ingredient", description = "Update an existing ingredient by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingredient updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid input: Please provide valid data"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Ingredient not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Ingredient not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> updateIngredient(@PathVariable Long id, @Valid @RequestBody IngredientsRequestDTO updatedIngredientDTO) {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Ingredients updatedIngredient = ingredientsService.update(id, mapper.toEntity(updatedIngredientDTO, users));

        return new ResponseEntity<>(mapper.toDto(updatedIngredient), HttpStatus.OK);
    }

    @GetMapping("/findAll")
    @Operation(summary = "Find All Ingredients", description = "Get a list of all ingredients.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of ingredients retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No content found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "No content found"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<List<IngredientsResponseDTO>> findAllIngredients() {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Ingredients> allIngredients = ingredientsService.findAllByUserId(users.getId());
        List<IngredientsResponseDTO> ingredientsResponseDTOS = new ArrayList<>();

        if (allIngredients.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        for (Ingredients ingredient : allIngredients) {
            ingredientsResponseDTOS.add(mapper.toDto(ingredient));
        }
        return new ResponseEntity<>(ingredientsResponseDTOS, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete Ingredient", description = "Delete an ingredient by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingredient deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Ingredient not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Ingredient not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> deleteIngredientById(@PathVariable Long id) {
        ingredientsService.delete(id);
        return ResponseEntity.ok("Ingredient deleted successfully");
    }
}
