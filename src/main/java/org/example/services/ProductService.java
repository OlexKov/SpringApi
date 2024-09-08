package org.example.services;
import org.example.dtos.ProductDto;
import org.example.entities.Product;
import org.example.exceptions.ProductException;
import org.example.interfaces.IProductRepository;
import org.example.interfaces.IProductService;
import org.example.interfaces.IStorageService;
import org.example.mapping.ProductMapper;
import org.example.models.FileFormats;
import org.example.models.PaginationResponse;
import org.example.models.ProductCreationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProductService implements IProductService {
    @Autowired
    private IProductRepository repo;
    @Autowired
    private IStorageService storageService;
    @Autowired
    private ProductMapper mapper;

    @Override
    public Long saveProduct(ProductCreationModel productModel) {
        try{
            Product product = mapper.fromCreationModel(productModel);
            String imageName = storageService.saveImage(productModel.getFile(), FileFormats.JPG);
            product.setImage(imageName);
            product.setCreationTime(LocalDateTime.now());
            Product savedProduct = repo.save(product);
            return savedProduct.getId();
        }
        catch (Exception e){
            throw new ProductException("Product save error");
        }
    }

    @Override
    public PaginationResponse<ProductDto> getProducts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(
                page, size, Sort.by("id").and(Sort.by("name")));
        Page<Product> productsPage = repo.findAll(pageRequest);
        Iterable<ProductDto> categories = mapper.toDto(productsPage.getContent());
        return  new PaginationResponse<ProductDto>(categories,productsPage.getTotalElements());
    }

    @Override
    public ProductDto getProductById(Long id) {
        Optional<Product> product = repo.findById(id);
        if(product.isPresent()){
            return mapper.toDto(product.get());
        }
        else{
            throw new ProductException("Invalid Product id");
        }
    }

    @Override
    public boolean deleteProductById(Long id) throws IOException {
        Optional<Product> optCategory =  repo.findById(id);
        boolean isPresent = optCategory.isPresent();
        if(isPresent){
            Product product = optCategory.get();
            repo.delete(product);
            storageService.deleteImage(product.getImage());
        }
        return  isPresent;
    }

    @Override
    public boolean updateProduct(ProductCreationModel productModel) throws IOException {
        Optional<Product> optProduct = repo.findById( productModel.getId());
        boolean isPresent = optProduct.isPresent();
        if(isPresent){
            Product product = mapper.fromCreationModel( productModel);
            product.setImage(optProduct.get().getImage());
            product.setCreationTime(LocalDateTime.now());
            if( productModel.getFile()!=null && ! productModel.getFile().isEmpty() ){
                storageService.deleteImage(optProduct.get().getImage());
                String imageName = storageService.saveImage( productModel.getFile(),FileFormats.WEBP);
                product.setImage(imageName);
            }
            repo.save(product);
        }
        return isPresent;
    }
}
