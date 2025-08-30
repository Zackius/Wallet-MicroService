package com.example.wallet_app.domain.customer.service;

import com.example.wallet_app.domain.customer.dto.CustomerDto;
import com.example.wallet_app.domain.customer.dto.SingleCustomerDto;
import com.example.wallet_app.dto.CustomResponse;
import com.example.wallet_app.exceptions.*;
import com.example.wallet_app.persistence.customer.entities.Customer;
import com.example.wallet_app.persistence.customer.repository.CustomerRepository;
import com.example.wallet_app.utils.TSIDGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.example.wallet_app.utils.Constants.SUCCESS;

@Service
@RequiredArgsConstructor
public class CustomerService {
private final CustomerRepository customerRepository;


public CustomResponse<SingleCustomerDto> createSingleCustomer(CustomerDto customerDto){


    // check if the customer is already present
    Optional<Customer> customer = customerRepository.findByName(customerDto.getName().toLowerCase());

    if(customer.isPresent()){
        throw new ConflictException("Customer already exists");
    }


    //create new customer
    LocalDateTime now = LocalDateTime.now();

    Customer customerItem = new Customer();
    customerItem.setName(customerDto.getName());
    customerItem.setCreatedAt(now);
    customerItem.setUpdatedAt(now);
    customerItem.setUuid(UUID.randomUUID());
    customerItem.setTsid(TSIDGenerator.generateTSID());
    customerItem.setCode(String.valueOf(UUID.randomUUID()));


    Customer newCustomer = customerRepository.save(customerItem);


    // return the object using a dto to mask db values
    SingleCustomerDto toReturnCustomer = new SingleCustomerDto();
    toReturnCustomer.setName(newCustomer.getName());
    toReturnCustomer.setCode(newCustomer.getCode());
    toReturnCustomer.setId(String.valueOf(newCustomer.getUuid()));

    return new  CustomResponse<SingleCustomerDto>(
            201, SUCCESS, toReturnCustomer);
}

public CustomResponse<SingleCustomerDto> getSingleCustomer(String uuid){


    Optional <Customer> customer = customerRepository.findByUuid(UUID.fromString(uuid));

    if(customer.isEmpty()){
        throw new NotFoundException("Customer not found");
    }


    // return the object using a dto to mask db values
    SingleCustomerDto toReturnCustomer = new SingleCustomerDto();
    toReturnCustomer.setName(customer.get().getName());
    toReturnCustomer.setCode(customer.get().getCode());
    toReturnCustomer.setId(String.valueOf(customer.get().getUuid()));

    return new  CustomResponse<>(
            201, SUCCESS, toReturnCustomer);

}


}
