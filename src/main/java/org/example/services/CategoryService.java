package org.example.services;

import org.example.dtos.CategoryDto;
import org.example.dtos.InvoiceDto;
import org.example.entities.Category;
import org.example.entities.Invoice;
import org.example.exceptions.CategoryException;
import org.example.interfaces.ICategoryRepository;
import org.example.interfaces.ICategoryService;
import org.example.interfaces.IStorageService;
import org.example.mapping.CategoryMapper;
import org.example.models.FileFormats;
import org.example.models.CategoryCreationModel;
import org.example.models.CategoryResponse;
import org.example.models.InvoiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CategoryService implements ICategoryService {

    @Autowired
    private ICategoryRepository repo;
    @Autowired
    private IStorageService storageService;
    @Autowired(required=true)
    private CategoryMapper mapper;

    @Override
    public Long saveCategory(CategoryCreationModel categoryModel) {
        try{
            Category category = mapper.fromCreationModel(categoryModel);
            String imageName = storageService.saveImage(categoryModel.getFile(), FileFormats.JPG);
            category.setImage(imageName);
            category.setCreationTime(LocalDateTime.now());
            Category savedCategory = repo.save(category);
            return savedCategory.getId();
        }
        catch (Exception e){
            throw new CategoryException("Invoice save error");
        }
    }

    @Override
    public CategoryResponse getCategoryByName(int page,int size,String name) {
        Page<Category> categoryPage = repo.findByNameContainingIgnoreCase(name, PageRequest.of(page, size));
        return new CategoryResponse(mapper.toDto(categoryPage.getContent()),categoryPage.getTotalElements());
    }

    public CategoryResponse getCategories(int page,int size) {
        PageRequest pageRequest = PageRequest.of(
                page, size,Sort.by("id").and(Sort.by("name")));
        Page<Category> categoriesPage = repo.findAll(pageRequest);
        Iterable<CategoryDto> categories = mapper.toDto(categoriesPage.getContent());
        return  new CategoryResponse(categories,categoriesPage.getTotalElements());
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Optional<Category> category = repo.findById(id);
        if(category.isPresent()){
            return mapper.toDto(category.get());
        }
        else{
            throw new CategoryException("Invalid category id");
        }
    }

    @Override
    public boolean deleteCategoryById(Long id) throws IOException {
        Optional<Category> optCategory =  repo.findById(id);
        boolean isPresent = optCategory.isPresent();
        if(isPresent){
            Category category = optCategory.get();
            repo.delete(category);
            storageService.deleteImage(category.getImage());
        }
        return  isPresent;
    }

    @Override
    public boolean updateCategory(CategoryCreationModel categoryModel) throws IOException {
        Optional<Category> optCategory = repo.findById(categoryModel.getId());
        boolean isPresent = optCategory.isPresent();
        if(isPresent){
            Category category = mapper.fromCreationModel(categoryModel);
            category.setImage(optCategory.get().getImage());
            category.setCreationTime(LocalDateTime.now());
            if(categoryModel.getFile()!=null && !categoryModel.getFile().isEmpty() ){
                storageService.deleteImage(optCategory.get().getImage());
                String imageName = storageService.saveImage(categoryModel.getFile(),FileFormats.JPG);
                category.setImage(imageName);
            }
            repo.save(category);
        }
        return isPresent;
    }
}
