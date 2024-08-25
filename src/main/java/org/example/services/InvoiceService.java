package org.example.services;

import org.example.entities.Invoice;
import org.example.exceptions.InvoiceNotFoundException;
import org.example.interfaces.IInvoiceService;
import org.example.interfaces.InvoiceRepository;
import org.example.models.InvoiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Service
public class InvoiceService implements IInvoiceService {
    @Autowired
    private InvoiceRepository repo;

    @Override
    public Invoice saveInvice(Invoice invoice) {
        return repo.save(invoice);
    }

    @Override
    public InvoiceResponse getInvoices(int page, int size) {
        PageRequest pageRequest = PageRequest.of(
                page, size, Sort.by("name").descending());
        Page<Invoice> invoicesPage = repo.findAll(pageRequest);
        return  new InvoiceResponse(invoicesPage.getContent(),invoicesPage.getTotalElements());
    }

    @Override
    public Optional<Invoice> getInvoiceById(Long id) {
        return repo.findById(id);
    }

    @Override
    public void deleteInvoiceById(Long id) {
        if(getInvoiceById(id).isPresent())
            repo.delete(getInvoiceById(id).get());
    }

    @Override
    public void updateInvoice(Invoice invoice) {
        repo.save(invoice);
    }
}
