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
public class LinkPaymentRequestPojo extends BasePaymentRequestPojo {

    @NotBlank(message = "Please ensure you set uniqueId")
    private String uniqueId;

    @NotBlank(message = "Please ensure you provide Payer PhoneNumber")
    private String payerPhoneNumber;

    @NotBlank(message = "Please ensure you provide Payer Name")
    private String payerName;

    @NotBlank(message = "Please ensure you provide Payer Email")
    private String payerEmail;

    @NotBlank(message = "Please ensure you provide Beneficiary Type")
    private String beneficiaryType;

    @NotBlank(message = "Please ensure you provide Account Type")
    private String accountType;

    @Valid
    @NotNull(message = "Please ensure you provide beneficiary details")
    private Beneficiary beneficiary;

    @Data
    public static class Beneficiary{

        @Valid
        @NotNull(message = "Please ensure you provide bank account details")
        private BankAccount bankAccount;

        @Data
        public static class BankAccount{

            @NotBlank(message = "Bank Account Name cannot be blank")
            private String name;

            @NotBlank(message = "Bank Account Id cannot be blank")
            private String bankId;

            @NotBlank(message = "Bank Account Number cannot be blank")
            private String accountNumber;
        }
    }
}


