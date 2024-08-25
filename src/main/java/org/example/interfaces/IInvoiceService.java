package org.example.interfaces;

import org.example.entities.Invoice;
import org.example.models.InvoiceResponse;

import java.util.Optional;

public interface IInvoiceService {
    public Invoice saveInvice(Invoice invoice);
    public InvoiceResponse getInvoices(int page, int size);
    public Optional<Invoice> getInvoiceById(Long id);
    public void deleteInvoiceById(Long id);
    public void updateInvoice(Invoice invoice);
}
