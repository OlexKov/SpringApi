package org.example.controllers;

import jakarta.validation.Valid;
import org.example.dtos.InvoiceDto;
import org.example.exceptions.InvoiceNotFoundException;
import org.example.interfaces.IInvoiceService;
import org.example.models.InvoiceCreationModel;
import org.example.models.PaginationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
    public ResponseEntity<String> saveInvoice(@Valid @ModelAttribute InvoiceCreationModel invoiceModel,BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors().toString());
        }
        try{
            return ResponseEntity.ok().body(invoiceService.saveInvoice(invoiceModel).toString());
        }catch(Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/get/{page}/{size}")
    public PaginationResponse<InvoiceDto> getAllInvoices(@PathVariable int page, @PathVariable int size) {
         return invoiceService.getInvoices(page,size);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<InvoiceDto> getInvoiceById(@PathVariable Long id) {
        InvoiceDto invoiceDto = invoiceService.getInvoiceById(id);
        return invoiceDto != null ? ResponseEntity.ok().body(invoiceDto)
                                  : ResponseEntity.notFound().build();
    }

    @PutMapping(value = "/update",consumes = "multipart/form-data")
    public ResponseEntity<String> updateInvoice( @Valid @ModelAttribute InvoiceCreationModel invoiceModel ,BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors().toString());
        }
        try{
            return invoiceService.updateInvoice(invoiceModel) ? ResponseEntity.ok().body(invoiceModel.getId().toString())
                                                              : ResponseEntity.badRequest().body("Invalid invoice id");
        }
        catch (Exception e){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteInvoice(@PathVariable Long id) {
        try {
          return  invoiceService.deleteInvoiceById(id) ?  ResponseEntity.ok().body(null)
                                                       :  ResponseEntity.badRequest().body("Invalid invoice id");
        } catch (InvoiceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
