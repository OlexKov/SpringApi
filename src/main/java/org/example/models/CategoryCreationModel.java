package org.example.models;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CategoryCreationModel {
    private Long id;
    private String name;
    private MultipartFile file;
    private String description;
}
