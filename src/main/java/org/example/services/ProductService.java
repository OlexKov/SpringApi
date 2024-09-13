package org.example.services;
import org.example.dtos.ProductDto;
import org.example.entities.Category;
import org.example.entities.Product;
import org.example.entities.ProductImage;
import org.example.exceptions.ProductException;
import org.example.interfaces.*;
import org.example.mapping.ProductMapper;
import org.example.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {
    @Autowired
    private IProductRepository repo;
    @Autowired
    private IStorageService storageService;
    @Autowired
    private ICategoryRepository categoryRepo;
    @Autowired
    private IImageRepository imageRepo;
    @Autowired
    private ProductMapper mapper;

    @Override
    public Long saveProduct(ProductCreationModel productModel) {
        try{
            Product product = mapper.fromCreationModel(productModel);
            List<ProductImage> images = new ArrayList<>();
            int index = 0;
            LocalDateTime date = LocalDateTime.now();
            for(var file:productModel.getFiles()){
                ProductImage image = new ProductImage(
                        0L,
                        storageService.saveImage(file, FileFormats.JPG),
                        index++,date,
                        false,
                        product);
                images.add(image);
            }
            product.setImages(images);
            product.setCreationTime(date);
            Optional<Category> category = categoryRepo.findById(productModel.getCategoryId());
            if(category.isPresent()){
                product.setCategory(category.get());
            }
            else{
                throw new ProductException("Invalid category id");
            }

            Product savedProduct = repo.save(product);
            return savedProduct.getId();
        }
        catch (Exception e){
            throw new ProductException("Product save error"+"\n" + e.getMessage());
        }
    }

    @Override
    public PaginationResponse<ProductDto> getProducts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(
                page, size, Sort.by("id"));
        Page<Product> productsPage = repo.findAll(pageRequest);
        Iterable<ProductDto> categories = mapper.toDto(productsPage.getContent());
        return  new PaginationResponse<ProductDto>(categories,productsPage.getTotalElements());
    }

    @Override
    public PaginationResponse<ProductDto> searchProducts(SearchData searchData) {

        PageRequest pageRequest = PageRequest.of(
                searchData.getPage()-1, searchData.getSize(), Sort.by(searchData.getSort()));
        Page<Product> productsPage = repo.searchProducts(searchData.getName(),searchData.getCategory(),searchData.getDescription(),pageRequest);
        Iterable<ProductDto> products = mapper.toDto(productsPage.getContent());
        return  new PaginationResponse<ProductDto>(products,productsPage.getTotalElements());
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
            storageService.deleteImages(product.getImages().stream().map(ProductImage::getName).toList());
        }
        return  isPresent;
    }

    @Override
    public boolean updateProduct(ProductUpdateModel productModel) throws IOException {
        Optional<Product> optProduct = repo.findById( productModel.getId());
        boolean isPresent = optProduct.isPresent();
        if(isPresent){
            Product product = mapper.fromUpdateModel(productModel);
            var oldImages = optProduct.get().getImages().toArray(ProductImage[]::new);

            var existingImages = new ArrayList<>(Arrays.stream(oldImages)
                    .filter(x -> Arrays.stream(productModel.getImages())
                    .anyMatch(z -> Objects.equals(z.getId(), x.getId())))
                    .sorted(Comparator.comparing(ProductImage::getId))
                    .toList());

            var modelImages = Arrays.stream(productModel.getImages())
                    .sorted(Comparator.comparing(ProductCreationImage::getId))
                    .toList();

            for (int i = 0; i < modelImages.size(); i++) {
                existingImages.get(i).setPriority( modelImages.get(i).getPriority());
            }

            product.setCreationTime(LocalDateTime.now());
            if(productModel.getFiles() != null) {
                for (var file : productModel.getFiles()) {
                    if (!file.getFile().isEmpty()) {
                        ProductImage image = new ProductImage(
                                0L,
                                storageService.saveImage(file.getFile(), FileFormats.WEBP),
                                file.getPriority(),
                                LocalDateTime.now(),
                                false,
                                product
                        );
                        existingImages.add(image);
                    }
                }
            }

            product.setImages(existingImages);
            Optional<Category> category = categoryRepo.findById(productModel.getCategoryId());
            if(category.isPresent()){
                product.setCategory(category.get());
            }
            else{
                throw new ProductException("Invalid category id");
            }
            repo.save(product);
            var imageToDelete =  Arrays.stream(oldImages)
                    .filter(x->Arrays.stream(existingImages.toArray(ProductImage[]::new))
                            .anyMatch(z-> !Objects.equals(z.getId(), x.getId())))
                    .toArray(ProductImage[]::new);
            imageRepo.deleteAll(List.of(imageToDelete));
            storageService.deleteImages(Arrays.stream(imageToDelete).map(ProductImage::getName).toList());
        }
        return isPresent;
    }
}
