package org.example.controllers;

import org.example.dtos.InvoiceDto;
import org.example.exceptions.InvoiceNotFoundException;
import org.example.interfaces.IInvoiceService;
import org.example.models.InvoiceCreationModel;
import org.example.models.InvoiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/invoice",produces = "application/json")
//@CrossOrigin(origins={
// "http://tacocloud:8080",
// "http://tacocloud.com"})
public class InvoiceController {

    @Autowired
    private IInvoiceService invoiceService;

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<Long> saveInvoice(@ModelAttribute InvoiceCreationModel invoiceModel) {
        try{
            return new ResponseEntity<>(invoiceService.saveInvoice( invoiceModel),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/{page}/{size}")
    public InvoiceResponse getAllInvoices(@PathVariable int page, @PathVariable int size) {
         return invoiceService.getInvoices(page,size);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<InvoiceDto> getInvoiceById(@PathVariable Long id) {
        InvoiceDto invoiceDto = invoiceService.getInvoiceById(id);
        return invoiceDto != null ? new ResponseEntity<>(invoiceDto, HttpStatus.OK)
                                  : new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/update",consumes = "multipart/form-data")
    public ResponseEntity<Long> updateInvoice(@ModelAttribute InvoiceCreationModel invoiceModel ) {
        try{
            return invoiceService.updateInvoice(invoiceModel) ? new ResponseEntity<>(invoiceModel.getId(),HttpStatus.OK)
                                                              : new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteInvoice(@PathVariable Long id) {
        try {
          return  invoiceService.deleteInvoiceById(id) ?  new ResponseEntity<>(null,HttpStatus.OK)
                                                       :  new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        } catch (InvoiceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
