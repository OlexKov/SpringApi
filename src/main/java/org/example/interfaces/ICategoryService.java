package org.example.interfaces;

import org.example.dtos.CategoryDto;
import org.example.dtos.InvoiceDto;
import org.example.models.CategoryCreationModel;
import org.example.models.CategoryResponse;
import org.example.models.InvoiceCreationModel;
import org.example.models.InvoiceResponse;

import java.io.IOException;

public interface ICategoryService {
    public Long saveCategory(CategoryCreationModel categoryModel);
    public CategoryResponse getCategoryByName(int page,int size,String name) ;
    public CategoryDto getCategoryById(Long id);
    public boolean deleteCategoryById(Long id) throws IOException;
    public boolean updateCategory(CategoryCreationModel categoryModel) throws IOException;
}
