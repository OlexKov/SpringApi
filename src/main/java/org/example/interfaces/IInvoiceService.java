package org.example.interfaces;

import org.example.dtos.InvoiceDto;
import org.example.entities.Invoice;
import org.example.models.InvoiceCreationModel;
import org.example.models.InvoiceResponse;

public interface IInvoiceService {
    public Long saveInvoice(InvoiceCreationModel invoiceModel);
    public InvoiceResponse getInvoices(int page, int size);
    public InvoiceDto getInvoiceById(Long id);
    public boolean deleteInvoiceById(Long id);
    public boolean updateInvoice(InvoiceCreationModel invoiceModel);
}
