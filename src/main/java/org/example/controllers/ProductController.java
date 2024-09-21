package org.example.controllers;

import jakarta.validation.Valid;
import org.example.dtos.ProductDto;
import org.example.exceptions.InvoiceNotFoundException;
import org.example.interfaces.IProductService;
import org.example.models.PaginationResponse;
import org.example.models.ProductCreationModel;
import org.example.models.SearchData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;

@RestController
@RequestMapping(value = "api/product",produces = "application/json")
public class ProductController {
    @Autowired
    private IProductService productService;

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<String> saveProduct(@Valid @ModelAttribute ProductCreationModel productModel, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors().toString());
        }
        try{
            Long id  = productService.saveProduct(productModel);
            return ResponseEntity.ok().body(id.toString());
        }catch(Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }


    @GetMapping("/get/{page}/{size}")
    public PaginationResponse<ProductDto> getProducts(@PathVariable int page, @PathVariable int size) {
        return productService.getProducts(page,size);
    }

    @PostMapping(value ="/get/{page}/{size}" )
    public PaginationResponse<ProductDto> getProducts(@PathVariable int page, @PathVariable int size,@RequestBody Long[] ids) {
        return productService.getProducts(page,size,ids);
    }


    @PostMapping(value ="/get",consumes = "multipart/form-data")
    public PaginationResponse<ProductDto> searchProducts(@ModelAttribute SearchData searchData) {
        return productService.searchProducts(searchData);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        try {
            ProductDto productDto = productService.getProductById(id);
            return productDto != null ? ResponseEntity.ok().body(productDto)
                    : ResponseEntity.notFound().build();
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping(value = "/update",consumes = "multipart/form-data")
    public ResponseEntity<String> updateProduct(@Valid @ModelAttribute ProductCreationModel productModel , BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors().toString());
        }
        try{
            return productService.updateProduct(productModel) ? ResponseEntity.ok().body(productModel.getId().toString())
                    : ResponseEntity.badRequest().body("Invalid invoice id");
        }
        catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        try {
            return  productService.deleteProductById(id) ?  ResponseEntity.ok().body(null)
                    :  ResponseEntity.badRequest().body("Invalid product id");
        } catch (InvoiceNotFoundException | IOException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
