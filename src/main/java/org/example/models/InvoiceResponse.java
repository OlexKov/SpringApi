package org.example.models;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.entities.Invoice;
import java.util.List;


@Data
@AllArgsConstructor
public class InvoiceResponse {
   private List<Invoice> invoicesList;
   private long totalElements ;
}
