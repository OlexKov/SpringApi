package org.example.models;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class InvoiceCreationModel  {
    private Long id;
    private String name;
    private String location;
    private Double amount;
    private MultipartFile file;
}
