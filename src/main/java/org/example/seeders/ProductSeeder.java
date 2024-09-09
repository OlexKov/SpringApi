package org.example.seeders;

import com.github.javafaker.Faker;
import org.example.entities.Category;
import org.example.entities.Product;
import org.example.entities.ProductImage;
import org.example.interfaces.ICategoryRepository;
import org.example.interfaces.IProductRepository;
import org.example.interfaces.IStorageService;
import org.example.models.FileFormats;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component
public class ProductSeeder implements CommandLineRunner {

    private final ICategoryRepository categoryRepository;
    private final IStorageService storageService;
    private final Faker faker = new Faker();
    public ProductSeeder(IStorageService storageService,ICategoryRepository categoryRepository) {
        this.storageService = storageService;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws IOException {
        if (categoryRepository.count() == 0) {
            List<Category> categories = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
               Category category = new Category(
                       null,
                       faker.commerce().productName(),
                       storageService.saveImage("https://picsum.photos/300/300", FileFormats.WEBP),
                       faker.lorem().sentence(10),
                       LocalDateTime.now(),
                       new ArrayList<Product>()
               );
                List<Product> products = new ArrayList<>();
                for (int s = 0; s < 5; s++) {
                    Product product = new Product(
                            null,
                            faker.commerce().productName(),
                            faker.lorem().sentence(10),
                            LocalDateTime.now(),
                            faker.number().randomDouble(2, 10, 100),
                            faker.number().randomDouble(2, 0, 20),
                            category,
                            new ArrayList<ProductImage>()
                    );
                    List<ProductImage> images = new ArrayList<ProductImage>();
                    for(int k = 0; k < 3; k++){
                        images.add(new ProductImage(
                                        null,
                                        storageService.saveImage("https://picsum.photos/300/300", FileFormats.WEBP),
                                        k,
                                        LocalDateTime.now(),
                                        false,
                                        product)
                        );
                    }
                    product.setImages(images);
                    products.add(product);
                }
                category.setProducts(products);
                categories.add(category);
            }
            categoryRepository.saveAll(categories);
        }
    }
}

