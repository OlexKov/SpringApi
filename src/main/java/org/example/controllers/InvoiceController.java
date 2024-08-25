package org.example.controllers;

import org.example.entities.Invoice;
import org.example.exceptions.InvoiceNotFoundException;
import org.example.interfaces.IInvoiceService;
import org.example.interfaces.ISorangeService;
import org.example.models.InvoiceCreationModel;
import org.example.models.InvoiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;

@RestController
@RequestMapping(value = "api/invoice",produces = "application/json")
//@CrossOrigin(origins={
// "http://tacocloud:8080",
// "http://tacocloud.com"})
public class InvoiceController {

    @Autowired
    private IInvoiceService service;
    @Autowired
    private ISorangeService storageService;


    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<Long> saveInvoice(@ModelAttribute InvoiceCreationModel invoiceModel) {
        try{
            Invoice invoice = new Invoice(
                invoiceModel.getId(),
                invoiceModel.getName(),
                invoiceModel.getLocation(),
                invoiceModel.getAmount(),
                storageService.saveFile(invoiceModel.getFile())
        );
            invoice = service.saveInvice(invoice);
            return new ResponseEntity<>(invoice.getId(),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/{page}/{size}")
    public InvoiceResponse getAllInvoices(@PathVariable int page, @PathVariable int size) {
         return service.getInvoices(page,size);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id) {
        Optional<Invoice> invoice = service.getInvoiceById(id);
        return invoice.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                      .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PutMapping(value = "/update",consumes = "multipart/form-data")
    public ResponseEntity<Long> updateInvoice(@ModelAttribute InvoiceCreationModel invoiceModel ) {
        try{
            Optional<Invoice> optInvoice = service.getInvoiceById(invoiceModel.getId());
            if(optInvoice.isPresent()){
                Invoice invoice = optInvoice.get();
                invoice.setName(invoiceModel.getName());
                invoice.setLocation(invoiceModel.getLocation());
                invoice.setAmount( invoiceModel.getAmount());
                if(!invoiceModel.getFile().isEmpty() ){
                    storageService.deleteFile(invoice.getFileName());
                    invoice.setFileName(storageService.saveFile(invoiceModel.getFile()));
                }
                service.updateInvoice(invoice);
                return new ResponseEntity<>(invoice.getId(),HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
            }

        }
        catch (Exception e){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteInvoice(@PathVariable Long id) {
        try {
            Optional<Invoice> invoice = service.getInvoiceById(id);
            if(invoice.isPresent()){
                String fileName = invoice.get().getFileName();
                service.deleteInvoiceById(id);
                storageService.deleteFile(fileName);
                return new ResponseEntity<>(null,HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
            }

        } catch (InvoiceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
