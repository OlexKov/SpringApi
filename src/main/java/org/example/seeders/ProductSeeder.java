package org.example.seeders;

import com.github.javafaker.Faker;
import org.example.entities.Category;
import org.example.entities.Product;
import org.example.entities.ProductImage;
import org.example.interfaces.ICategoryRepository;
import org.example.interfaces.IStorageService;
import org.example.models.FileFormats;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
            int categoryCount = 10;
            int productsPerCategoryCount = 5;
            int imagesPerProductCount = 3;
            int imageCount = categoryCount * (1 + (productsPerCategoryCount * imagesPerProductCount));
            ExecutorService executor = Executors.newFixedThreadPool(20);
            List<CompletableFuture<String>> imagesFutures = new ArrayList<>();
            for (int i = 0; i < imageCount; i++) {
                imagesFutures.add(
                        CompletableFuture.supplyAsync(() -> {
                            try {
                                return storageService.saveImage("https://picsum.photos/300/300", FileFormats.WEBP);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }, executor)
                );
            }

            // Очікуємо завершення всіх завантажень зображень
            CompletableFuture<Void> allImages = CompletableFuture.allOf(imagesFutures.toArray(new CompletableFuture[0]));

            // Після завершення завантаження всіх зображень
            allImages.thenRun(() -> {
                List<String> imagesUrls = imagesFutures.parallelStream()
                        .map(CompletableFuture::join)
                        .toList();
                executor.shutdown();
                List<Category> categories = new ArrayList<>();
                int imageIndex = 0;

                for (int i = 0; i < categoryCount; i++) {
                    // Створюємо нову категорію
                    Category category = new Category(
                            null,
                            faker.commerce().productName(),
                            imagesUrls.get(imageIndex++),
                            faker.lorem().sentence(10),
                            LocalDateTime.now(),
                            new ArrayList<>()
                    );

                    List<Product> products = new ArrayList<>();
                    for (int j = 0; j < productsPerCategoryCount; j++) {
                        // Створюємо новий продукт
                        Product product = new Product(
                                null,
                                faker.commerce().productName(),
                                faker.lorem().sentence(10),
                                LocalDateTime.now(),
                                faker.number().randomDouble(2, 10, 100),
                                faker.number().randomDouble(2, 0, 20),
                                category,
                                new ArrayList<>()
                        );

                        List<ProductImage> images = new ArrayList<>();
                        for (int k = 0; k < imagesPerProductCount; k++) {
                            // Використовуємо наступне зображення для продукту
                            images.add(new ProductImage(
                                    null,
                                    imagesUrls.get(imageIndex++),
                                    k,
                                    LocalDateTime.now(),
                                    false,
                                    product
                            ));
                        }

                        product.setImages(images);
                        products.add(product);
                    }

                    category.setProducts(products);
                    categories.add(category);
                }

                // Збереження категорій з продуктами і зображеннями в базу даних
                categoryRepository.saveAll(categories);
                System.out.println("Сид бази даних завершено!");
            }).exceptionally(ex -> {
                System.err.println("Помилка при збереженні категорій: " + ex.getMessage());
                return null;
            });


        }
    }
}

