package org.example.models;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductCreationFile {
    private MultipartFile file;
    private int priority;
}
