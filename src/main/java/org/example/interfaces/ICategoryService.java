package org.example.interfaces;

import org.example.dtos.CategoryDto;
import org.example.dtos.InvoiceDto;
import org.example.models.CategoryCreationModel;
import org.example.models.CategoryResponse;
import org.example.models.InvoiceCreationModel;
import org.example.models.InvoiceResponse;

import java.io.IOException;

public interface ICategoryService {
    Long saveCategory(CategoryCreationModel categoryModel);
    CategoryResponse getCategoryByName(int page,int size,String name) ;
    CategoryResponse getCategories(int page,int size);
    CategoryDto getCategoryById(Long id);
    boolean deleteCategoryById(Long id) throws IOException;
    boolean updateCategory(CategoryCreationModel categoryModel) throws IOException;
}
