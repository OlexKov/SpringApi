package org.example.models;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.dtos.InvoiceDto;
import org.example.entities.Invoice;
import java.util.List;


@Data
@AllArgsConstructor
public class InvoiceResponse {
   private Iterable<InvoiceDto> invoicesList;
   private long totalElements ;
}
