package com.example.wallet_app.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class Constants {

    public static Double ZERO = 0.0;

    public static final String SUCCESS = "SUCCESS";
    public static final String PENDING = "PENDING";
    public static final String FAILED = "FAILED";


    public static final String CREATED_SUCCESSFULLY = "Created Successfully";
    public static final String UPDATED_SUCCESSFULLY = "Updated Successfully";
    public static final String DELETED_SUCCESSFULLY = "Deleted Successfully";
    /*
     * Error Messages
     */
    public static final String NOT_AUTHORIZED = "Not Authorized";
    public static final String CUSTOMER_NOT_FOUND = "Customer with the provided Id is not found";
    public static final String INVALID_AMOUNT = "Invalid amount";
    public static final String REQUEST_ID_REQUIRED = "Request Id is required";
    public static final String USER_NOT_FOUND = "User Not Found";

    // date

    public static final LocalDateTime now = LocalDateTime.now();

}
