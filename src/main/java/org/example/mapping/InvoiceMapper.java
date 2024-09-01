package org.example.mapping;

import org.example.dtos.InvoiceDto;
import org.example.entities.Invoice;
import org.example.models.InvoiceCreationModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    @Mapping(target = "fileName", ignore = true)
    Invoice formCreationModel(InvoiceCreationModel model);
    Invoice formDto(InvoiceDto invoiceDto);
    InvoiceDto toDto(Invoice invoice);
    Iterable<InvoiceDto> toDto(Iterable<Invoice> invoice);
}
