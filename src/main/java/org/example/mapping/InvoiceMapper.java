package org.example.mapping;

import org.example.dtos.InvoiceDto;
import org.example.entities.Invoice;
import org.example.models.InvoiceCreationModel;

import java.util.*;

public class InvoiceMapper {
    public static Invoice formCreationModel(InvoiceCreationModel model){
        return new Invoice(
                model.getId(),
                model.getName(),
                model.getLocation(),
                model.getAmount(),""
        );
    }
    public static Invoice formDto(InvoiceDto invoiceDto){
        return new Invoice(
                invoiceDto.getId(),
                invoiceDto.getName(),
                invoiceDto.getLocation(),
                invoiceDto.getAmount(),
                invoiceDto.getFileName()
        );
    }
    public static InvoiceDto toDto(Invoice invoice){
        return new InvoiceDto(
                invoice.getId(),
                invoice.getName(),
                invoice.getLocation(),
                invoice.getAmount(),
                invoice.getFileName()
        );
    }

    public static Iterable<InvoiceDto> toDto(Iterable<Invoice> invoices){
        List<InvoiceDto> dtos = new ArrayList<InvoiceDto>() ;
        for(Invoice invoice : invoices){
            dtos.add(new InvoiceDto(
                    invoice.getId(),
                    invoice.getName(),
                    invoice.getLocation(),
                    invoice.getAmount(),
                    invoice.getFileName()
            ));
        }   
        return dtos;
    }
}
