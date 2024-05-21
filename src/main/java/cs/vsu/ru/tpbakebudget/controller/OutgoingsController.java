package cs.vsu.ru.tpbakebudget.controller;

import cs.vsu.ru.tpbakebudget.dto.request.outgoings.OutgoingsRequestDTO;
import cs.vsu.ru.tpbakebudget.dto.responce.outgoings.OutgoingsResponseDTO;
import cs.vsu.ru.tpbakebudget.mapper.OutgoingsMapper;
import cs.vsu.ru.tpbakebudget.model.Outgoings;
import cs.vsu.ru.tpbakebudget.model.Products;
import cs.vsu.ru.tpbakebudget.service.impl.OutgoingsServiceImpl;
import cs.vsu.ru.tpbakebudget.service.impl.ProductsServiceImpl;
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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/outgoings")
@SecurityRequirement(name = "bearerAuth")
public class OutgoingsController {

    private final OutgoingsServiceImpl outgoingsService;

    private final ProductsServiceImpl productsService;

    private final OutgoingsMapper mapper;

    @Autowired
    public OutgoingsController(OutgoingsServiceImpl outgoingsService, ProductsServiceImpl productsService, OutgoingsMapper mapper) {
        this.outgoingsService = outgoingsService;
        this.productsService = productsService;
        this.mapper = mapper;
    }

    @PostMapping("/create/{productId}")
    @Operation(summary = "Create Outgoing", description = "Create a new Outgoing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Outgoing created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid input: Please provide valid data"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Product not found"))),
            @ApiResponse(responseCode = "409", description = "Conflict - Outgoing already exists for this product",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Conflict: Outgoing already exists for this product"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> createOutgoing(@PathVariable Long productId, @Valid @RequestBody OutgoingsRequestDTO outgoingsRequestDTO) {
        Products product = productsService.findById(productId);

        Outgoings outgoing = mapper.toEntity(outgoingsRequestDTO, product);
        Outgoings createdOutgoing = outgoingsService.save(outgoing);
        if(createdOutgoing != null){
            return new ResponseEntity<>(mapper.toDto(createdOutgoing), HttpStatus.CREATED);
        }
        else{
            return new ResponseEntity<>("Outgoing already exists for this product", HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Get Outgoing by ID", description = "Get an Outgoing by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Outgoing found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Outgoing not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Outgoing not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<OutgoingsResponseDTO> getOutgoingById(@PathVariable Long id) {
        Outgoings outgoings = outgoingsService.findById(id);
        return ResponseEntity.ok(mapper.toDto(outgoings));
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update Outgoing", description = "Update an existing Outgoing by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Outgoing updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid input: Please provide valid data"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Outgoing not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Outgoing not found"))),
            @ApiResponse(responseCode = "409", description = "Conflict - Outgoing already exists for this product",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Conflict: Outgoing already exists for this product"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> updateOutgoing(@PathVariable Long id, @Valid @RequestBody OutgoingsRequestDTO updatedOutgoingDTO) {
        Outgoings oldOutgoing = outgoingsService.findById(id);

        Outgoings updatedOutgoing = outgoingsService.update(id, mapper.toEntity(updatedOutgoingDTO, oldOutgoing.getProduct()));
        if(updatedOutgoing != null){
            return new ResponseEntity<>(mapper.toDto(updatedOutgoing), HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("Outgoing already exists for this product", HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/findAll/{productId}")
    @Operation(summary = "Find All Outgoings", description = "Get a list of all Outgoings.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of Outgoings retrieved successfully"),
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
    public ResponseEntity<List<OutgoingsResponseDTO>> findAllOutgoings(@PathVariable Long productId) {
        List<Outgoings> allOutgoings = outgoingsService.findAllByProductId(productId);
        List<OutgoingsResponseDTO> outgoingsResponseDTOS = new ArrayList<>();

        if (allOutgoings.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        for (Outgoings outgoing : allOutgoings) {
            outgoingsResponseDTOS.add(mapper.toDto(outgoing));
        }
        return new ResponseEntity<>(outgoingsResponseDTOS, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete Outgoing", description = "Delete an Outgoing by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Outgoing deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Outgoing not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Outgoing not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> deleteOutgoingById(@PathVariable Long id) {
        outgoingsService.findById(id);
        outgoingsService.delete(id);
        return ResponseEntity.ok("Outgoing deleted successfully");
    }
}
