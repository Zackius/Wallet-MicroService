package com.example.wallet_app.controllers;


import com.example.wallet_app.domain.customer.dto.CustomerDto;
import com.example.wallet_app.domain.customer.dto.SingleCustomerDto;
import com.example.wallet_app.domain.customer.service.CustomerService;
import com.example.wallet_app.dto.CustomResponse;
import com.example.wallet_app.persistence.customer.entities.Customer;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;


    @PostMapping("new")
    public ResponseEntity<CustomResponse<SingleCustomerDto>> newCustomer(@RequestBody CustomerDto customerDto){
        CustomResponse<SingleCustomerDto> response = customerService.createSingleCustomer(customerDto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    @GetMapping("single/{uuid}")
    public ResponseEntity<CustomResponse<SingleCustomerDto>> getCustomer(@PathVariable String uuid){
        CustomResponse<SingleCustomerDto> response = customerService.getSingleCustomer(uuid);
        return ResponseEntity.status(response.getStatus()).body(response);
    }







}
