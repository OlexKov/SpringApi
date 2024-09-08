package org.example.controllers;

import jakarta.validation.Valid;
import org.example.dtos.CategoryDto;
import org.example.exceptions.InvoiceNotFoundException;
import org.example.interfaces.ICategoryService;
import org.example.models.CategoryCreationModel;
import org.example.models.PaginationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(value = "api/category",produces = "application/json")
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<String> saveCategory( @ModelAttribute CategoryCreationModel categoryModel) {
//        if (bindingResult.hasErrors()) {
//            return ResponseEntity.badRequest().body(bindingResult.getAllErrors().toString());
//        }
        try{
            Long id  = categoryService.saveCategory(categoryModel);
            return ResponseEntity.ok().body(id.toString());
        }catch(Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/get/{page}/{size}/{name}")
    public PaginationResponse<CategoryDto> getCategoriesByName(@PathVariable int page, @PathVariable int size, @PathVariable String name) {
        return categoryService.getCategoryByName(page,size,name);
    }

    @GetMapping("/get/{page}/{size}")
    public PaginationResponse<CategoryDto> getCategories(@PathVariable int page, @PathVariable int size) {
        return categoryService.getCategories(page,size);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<CategoryDto> getInvoiceById(@PathVariable Long id) {
        CategoryDto categoryDto = categoryService.getCategoryById(id);
        return categoryDto != null ? ResponseEntity.ok().body(categoryDto)
                : ResponseEntity.notFound().build();
    }

    @PutMapping(value = "/update",consumes = "multipart/form-data")
    public ResponseEntity<String> updateInvoice( @Valid @ModelAttribute CategoryCreationModel categoryModel ,BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors().toString());
        }
        try{
            return categoryService.updateCategory(categoryModel) ? ResponseEntity.ok().body(categoryModel.getId().toString())
                    : ResponseEntity.badRequest().body("Invalid invoice id");
        }
        catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteInvoice(@PathVariable Long id) {
        try {
            return  categoryService.deleteCategoryById(id) ?  ResponseEntity.ok().body(null)
                    :  ResponseEntity.badRequest().body("Invalid invoice id");
        } catch (InvoiceNotFoundException | IOException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
