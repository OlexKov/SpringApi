package org.example.services;

import org.example.dtos.InvoiceDto;
import org.example.entities.Invoice;
import org.example.exceptions.InvoiceNotFoundException;
import org.example.interfaces.IInvoiceService;
import org.example.interfaces.IStorageService;
import org.example.interfaces.InvoiceRepository;
import org.example.mapping.InvoiceMapper;
import org.example.models.InvoiceCreationModel;
import org.example.models.PaginationResponse;
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
    @Autowired
    private IStorageService storageService;
    @Autowired
    private InvoiceMapper mapper;

    @Override
    public Long saveInvoice(InvoiceCreationModel invoiceModel) {
        try{
            Invoice invoice = mapper.formCreationModel(invoiceModel);
            invoice.setFileName(storageService.saveFile(invoiceModel.getFile()));
            Invoice savedInvoice = repo.save(invoice);
            return savedInvoice.getId();
        }
        catch (Exception e){
            throw new RuntimeException("Invoice save error");
        }
    }

    @Override
    public PaginationResponse<InvoiceDto> getInvoices(int page, int size) {
        PageRequest pageRequest = PageRequest.of(
                page, size, Sort.by("name").descending());
        Page<Invoice> invoicesPage = repo.findAll(pageRequest);
        Iterable<InvoiceDto> invoices = mapper.toDto(invoicesPage.getContent());
        return  new PaginationResponse<InvoiceDto>(invoices,invoicesPage.getTotalElements());
    }

    @Override
    public InvoiceDto getInvoiceById(Long id) {
        Optional<Invoice> invoice = repo.findById(id);
        if(invoice.isPresent()){
            return mapper.toDto(invoice.get());
        }
        else{
            throw new InvoiceNotFoundException("Invalid invoice id");
        }

    }

    @Override
    public boolean deleteInvoiceById(Long id) {
        Optional<Invoice> optInvoice =  repo.findById(id);
        boolean isPresent = optInvoice.isPresent();
        if(isPresent){
            Invoice invoice = optInvoice.get();
            repo.delete(invoice);
            storageService.deleteFile(invoice.getFileName());
        }
        return  isPresent;
    }

    @Override
    public boolean updateInvoice(InvoiceCreationModel invoiceModel) {
        Optional<Invoice> optInvoice = repo.findById(invoiceModel.getId());
        boolean isPresent = optInvoice.isPresent();
        if(isPresent){
            Invoice invoice = mapper.formCreationModel(invoiceModel);
            if(!invoiceModel.getFile().isEmpty() ){
                storageService.deleteFile(optInvoice.get().getFileName());
                invoice.setFileName(storageService.saveFile(invoiceModel.getFile()));
            }
            repo.save(invoice);
        }
        return isPresent;
    }
}
