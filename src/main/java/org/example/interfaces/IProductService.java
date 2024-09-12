package org.example.interfaces;


import org.example.dtos.ProductDto;
import org.example.models.PaginationResponse;
import org.example.models.ProductCreationModel;
import org.example.models.ProductUpdateModel;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public interface IProductService {
    Long saveProduct(ProductCreationModel productModel);
    PaginationResponse<ProductDto> getProducts(int page,int size);
    ProductDto getProductById(Long id);
    boolean deleteProductById(Long id) throws IOException;
    boolean updateProduct(ProductUpdateModel productModel) throws IOException;
}
