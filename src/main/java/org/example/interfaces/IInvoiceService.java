package org.example.interfaces;

import org.example.dtos.InvoiceDto;
import org.example.models.InvoiceCreationModel;
import org.example.models.PaginationResponse;

public interface IInvoiceService {
    public Long saveInvoice(InvoiceCreationModel invoiceModel);
    public PaginationResponse<InvoiceDto> getInvoices(int page, int size);
    public InvoiceDto getInvoiceById(Long id);
    public boolean deleteInvoiceById(Long id);
    public boolean updateInvoice(InvoiceCreationModel invoiceModel);
}
