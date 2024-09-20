package org.example.dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InvoiceDto {
    private Long id;
    private String name;
    private String location;
    private Double amount;
    private String fileName;
}
