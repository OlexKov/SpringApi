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
            product.setId(0L);
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
        Sort.Direction direction = Objects.equals(searchData.getSortDir(), "descend") ? Sort.Direction.DESC: Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(
                searchData.getPage()-1, searchData.getSize(), Sort.by(direction,searchData.getSort()));
        Page<Product> productsPage = repo.searchProducts(searchData.getName(),searchData.getCategories(),searchData.getDescription(),pageRequest);
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
            storageService.deleteImages(product.getImages()
                    .stream()
                    .map(ProductImage::getName)
                    .toList());
        }
        return  isPresent;
    }

    @Override
    public boolean updateProduct(ProductCreationModel productModel) throws IOException {
        Optional<Product> optProduct = repo.findById( productModel.getId());
        boolean isPresent = optProduct.isPresent();
        if(isPresent){
            Product product = mapper.fromCreationModel(productModel);
            var oldImages = new ArrayList<>(optProduct.get().getImages());
            product.setCreationTime(LocalDateTime.now());
            List<ProductImage> newImagesList = new ArrayList<ProductImage>() ;
            if(productModel.getFiles() != null) {
                int index = -1;
                for (var file : productModel.getFiles()) {
                    index++;
                    if(!Objects.equals(file.getContentType(), "old-image")){
                        if(!file.isEmpty()){
                            ProductImage image = new ProductImage(
                                    0L,
                                    storageService.saveImage(file, FileFormats.WEBP),
                                    index,
                                    LocalDateTime.now(),
                                    false,
                                    product
                            );
                            newImagesList.add(image);
                        }
                    }
                    else {
                        var imageName = file.getOriginalFilename();
                        var oldImage = Arrays.stream(oldImages.toArray(ProductImage[]::new))
                                .filter(x-> Objects.equals(x.getName(), imageName))
                                .findFirst();
                        if(oldImage.isPresent()){
                            var image = oldImage.get();
                            oldImages.remove(image);
                            image.setPriority(index);
                            newImagesList.add(image);
                        }
                    }
               }
                product.setImages(newImagesList);
            }
            else{
                throw new ProductException("Images is absent");
            }
            if(!Objects.equals(optProduct.get().getCategory().getId(), productModel.getCategoryId()))
            {
                Optional<Category> category = categoryRepo.findById(productModel.getCategoryId());
                if(category.isPresent()){
                    product.setCategory(category.get());
                }
                else{
                    throw new ProductException("Invalid category id");
                }
            }
            else{
                product.setCategory(optProduct.get().getCategory());
            }
            repo.save(product);
            if(!oldImages.isEmpty()){
                imageRepo.deleteAll(oldImages);
                var imagesToDelete = Arrays.stream(oldImages.toArray(ProductImage[]::new)).map(ProductImage::getName).toList();
                storageService.deleteImages(imagesToDelete);
            }
        }
        return isPresent;
    }
}
