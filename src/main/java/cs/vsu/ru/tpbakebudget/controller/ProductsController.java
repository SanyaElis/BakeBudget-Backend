package cs.vsu.ru.tpbakebudget.controller;

import cs.vsu.ru.tpbakebudget.dto.request.products.IngredientsInProductRequestDTO;
import cs.vsu.ru.tpbakebudget.dto.request.products.ProductsRequestDTO;
import cs.vsu.ru.tpbakebudget.dto.responce.products.IngredientsInProductResponseDTO;
import cs.vsu.ru.tpbakebudget.dto.responce.products.ProductsResponseDTO;
import cs.vsu.ru.tpbakebudget.exception.NotFoundException;
import cs.vsu.ru.tpbakebudget.mapper.IngredientsInProductMapper;
import cs.vsu.ru.tpbakebudget.mapper.ProductsMapper;
import cs.vsu.ru.tpbakebudget.minio.MinioTemplate;
import cs.vsu.ru.tpbakebudget.minio.ProductPicture;
import cs.vsu.ru.tpbakebudget.model.*;
import cs.vsu.ru.tpbakebudget.service.impl.IngredientsInProductServiceImpl;
import cs.vsu.ru.tpbakebudget.service.impl.IngredientsServiceImpl;
import cs.vsu.ru.tpbakebudget.service.impl.ProductsServiceImpl;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/products")
@SecurityRequirement(name = "bearerAuth")
public class ProductsController {

    private final ProductsServiceImpl productsService;

    private final IngredientsServiceImpl ingredientsService;

    private final IngredientsInProductServiceImpl ingredientsInProductService;

    private final ProductsMapper productsMapper;

    private final IngredientsInProductMapper ingredientsInProductMapper;

    private final MinioTemplate template;

    public ProductsController(ProductsServiceImpl productsService, IngredientsServiceImpl ingredientsService, IngredientsInProductServiceImpl ingredientsInProductService, ProductsMapper productsMapper, IngredientsInProductMapper ingredientsInProductMapper, MinioTemplate template) {
        this.productsService = productsService;
        this.ingredientsService = ingredientsService;
        this.ingredientsInProductService = ingredientsInProductService;
        this.productsMapper = productsMapper;
        this.ingredientsInProductMapper = ingredientsInProductMapper;
        this.template = template;
    }

    @PostMapping("/create")
    @Operation(summary = "Create Product", description = "Create a new product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
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
    public ResponseEntity<ProductsResponseDTO> createProduct(@Valid @RequestBody ProductsRequestDTO productsRequest) {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Products product = productsMapper.toEntity(productsRequest, users);
        Products createdProduct = productsService.save(product);

        return new ResponseEntity<>(productsMapper.toDto(createdProduct), HttpStatus.CREATED);
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Get Product by ID", description = "Get an products by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Ingredient not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<ProductsResponseDTO> getProductById(@PathVariable Long id) {
        Products product = productsService.findById(id);
        return ResponseEntity.ok(productsMapper.toDto(product));
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update Product", description = "Update an existing product by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
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
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductsRequestDTO updatedProductDTO) {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Products updatedProduct = productsService.update(id, productsMapper.toEntity(updatedProductDTO, users));
        return new ResponseEntity<>(productsMapper.toDto(updatedProduct), HttpStatus.OK);
    }

    @GetMapping("/findAll")
    @Operation(summary = "Find All Products", description = "Get a list of all products.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products retrieved successfully"),
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
    public ResponseEntity<List<ProductsResponseDTO>> findAllProducts() {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Products> allProducts = productsService.findAllByUserId(users.getId());
        List<ProductsResponseDTO> productsResponseDTOS = new ArrayList<>();

        if (allProducts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        for (Products product : allProducts) {
            productsResponseDTOS.add(productsMapper.toDto(product));
        }
        return new ResponseEntity<>(productsResponseDTOS, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete Product", description = "Delete an product by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Product not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> deleteProductById(@PathVariable Long id) {
        productsService.findById(id);
        productsService.delete(id);
        return ResponseEntity.ok("Product deleted successfully");
    }

    @PutMapping(value = "/uploadPicture/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload Product Picture", description = "Upload a picture for a product by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product picture added successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Product not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> uploadPicture(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Products product = productsService.findById(id);
        ProductPicture picture = new ProductPicture(file.getBytes(), Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".") + 1));
        productsService.setMinioName(product, picture.getMinioObjectName());
        template.uploadFile(picture);

        return ResponseEntity.ok("Product picture added successfully");
    }

    @PostMapping(value = "/reloadPicture/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Reload Product Picture", description = "Reload a picture for a product by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product picture reloaded successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Product not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> reloadPicture(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Products product = productsService.findById(id);
        ProductPicture picture = new ProductPicture(file.getBytes(), Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".") + 1));
        template.deleteFile(product.getMinioPictureName());
        productsService.setMinioName(product, picture.getMinioObjectName());
        template.uploadFile(picture);

        return ResponseEntity.ok("Product picture reloaded successfully");
    }

    @GetMapping(value = "/getPicture/{id}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    @Operation(summary = "Get Product Picture", description = "Get the picture of a product by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product picture retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Product not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<byte[]> getPicture(@PathVariable Long id) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Products product = productsService.findById(id);
        if (product.getMinioPictureName() == null) {
            throw new NotFoundException("Product picture not found");
        }

        InputStream inputStream = template.downloadFile(product.getMinioPictureName());
        byte[] imageBytes = IOUtils.toByteArray(inputStream);
        inputStream.close();
        return new ResponseEntity<>(imageBytes, HttpStatus.OK);
    }

    @DeleteMapping(value = "/deletePicture/{id}")
    @Operation(summary = "Delete Product Picture", description = "Delete the picture of a product by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product picture deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Product not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> deletePicture(@PathVariable Long id) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Products product = productsService.findById(id);
        template.deleteFile(product.getMinioPictureName());
        productsService.setMinioName(product, null);
        return ResponseEntity.ok("Product picture deleted successfully");
    }

    @PostMapping("/addIngredient")
    @Operation(summary = "Add an ingredient", description = "Add an ingredient in product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ingredient added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid input: Please provide valid data"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Entity not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Entity not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> addIngredient(@Valid @RequestBody IngredientsInProductRequestDTO ingredientsInProductRequestDTO) {
        Products products = productsService.findById(ingredientsInProductRequestDTO.getProductId());
        Ingredients ingredients = ingredientsService.findById(ingredientsInProductRequestDTO.getIngredientId());
        IngredientsInProduct ingredientsInProduct = ingredientsInProductMapper.toEntity(ingredients, products, ingredientsInProductRequestDTO.getWeight());
        ingredientsInProductService.save(ingredientsInProduct);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/updateIngredient")
    @Operation(summary = "Update Ingredient in product", description = "Update an existing Ingredient in product by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingredient in product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid input: Please provide valid data"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Entity not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Entity not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> updateProduct(@Valid @RequestBody IngredientsInProductRequestDTO ingredientsInProductRequestDTO) {
        Products products = productsService.findById(ingredientsInProductRequestDTO.getProductId());
        Ingredients ingredients = ingredientsService.findById(ingredientsInProductRequestDTO.getIngredientId());
        IngredientsInProduct newIngredientsInProduct = ingredientsInProductMapper.toEntity(ingredients, products, ingredientsInProductRequestDTO.getWeight());
        ingredientsInProductService.update(newIngredientsInProduct);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/findAllIngredients/{id}")
    @Operation(summary = "Find All Ingredients in Product", description = "Get a list of all ingredients in product.")
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
    public ResponseEntity<List<IngredientsInProductResponseDTO>> findAllIngredientsInProduct(@PathVariable Long id) {
        List<IngredientsInProduct> ingredients = ingredientsInProductService.findByPk_ProductId(id);
        List<IngredientsInProductResponseDTO> responseDTOS = new ArrayList<>();
        if (ingredients.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        for (IngredientsInProduct ingredient : ingredients) {
            responseDTOS.add(ingredientsInProductMapper.toDto(ingredient));
        }
        return new ResponseEntity<>(responseDTOS, HttpStatus.OK);
    }

    @DeleteMapping("/deleteIngredient")
    @Operation(summary = "Delete Ingredient in product", description = "Delete an Ingredient in product by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingredient in product deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "Entity not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Entity not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> deleteIngredientById(@Valid @RequestBody IngredientsInProductRequestDTO ingredientsInProductRequestDTO) {
        Products product = productsService.findById(ingredientsInProductRequestDTO.getProductId());
        Ingredients ingredient = ingredientsService.findById(ingredientsInProductRequestDTO.getIngredientId());

        IngredientsInProductKey key = new IngredientsInProductKey(ingredient, product);
        ingredientsInProductService.delete(key);
        return ResponseEntity.ok("Ingredient deleted successfully");
    }
}
