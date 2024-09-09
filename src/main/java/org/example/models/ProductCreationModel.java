package org.example.models;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
@AllArgsConstructor
public class ProductCreationModel {
    private Long id;
    private String name;
    private MultipartFile[] files;
    private String description;
    private double price;
    private double discount;
}
