package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaginationResponse<T>  {
    private Iterable<T> invoicesList;
    private long totalElements ;
}
