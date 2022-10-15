package com.keanhive.stich.api.integration.restcall.request;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author KeanHive
 * @Date 14/10/2022
 */

@Slf4j
@Data
public class InstantPaymentRequestPojo extends BasePaymentRequestPojo {

    @Valid
    @NotNull(message = "Please ensure you provide beneficiary details")
    public Beneficiary beneficiary;

    @Data
    public static class Beneficiary{

        @Valid
        @NotNull(message = "Please ensure you provide bank account details")
        public BankAccount bankAccount;

        @Data
        public static class BankAccount{

            @NotBlank(message = "Bank Account Name cannot be blank")
            public String name;

            @NotBlank(message = "Bank Account Id cannot be blank")
            public String bankId;

            @NotBlank(message = "Bank Account Number cannot be blank")
            public String accountNumber;
        }
    }
}


