package org.example.seeders;

import com.github.javafaker.Faker;
import org.example.entities.Product;
import org.example.interfaces.IProductRepository;
import org.example.interfaces.IProductService;
import org.example.interfaces.IStorageService;
import org.example.models.FileFormats;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Component
public class ProductSeeder implements CommandLineRunner {

    private final IProductRepository productRepository;
    private final IStorageService storageService;
    private final Faker faker = new Faker();
    public ProductSeeder(IProductRepository productRepository,IStorageService storageService) {
        this.productRepository = productRepository;
        this.storageService = storageService;
    }

    @Override
    public void run(String... args) throws IOException {
        if (productRepository.count() == 0) {
            List<Product> products = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Product product = new Product(
                        null,
                        faker.commerce().productName(),
                        storageService.saveImage("https://picsum.photos/300/300", FileFormats.WEBP),
                        faker.lorem().sentence(10),
                        LocalDateTime.now(),
                        faker.number().randomDouble(2, 10, 100),
                        faker.number().randomDouble(2, 0, 20)
                );
                products.add(product);
            }
            productRepository.saveAll(products);
        }
    }
}

