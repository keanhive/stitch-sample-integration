package com.keanhive.stich.api.integration.restcall.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author KeanHive
 * @Date 14/10/2022
 */

@Data
public class BasePaymentRequestPojo {

    @Valid
    @NotNull(message = "Amount cannot be null")
    public Amount amount;

    @NotBlank(message = "Payer Reference cannot be blank")
    public String payerReference;

    @NotBlank(message = "Beneficiary Reference cannot be blank")
    public String beneficiaryReference;

    @NotBlank(message = "External Reference cannot be blank")
    public String externalReference;

    @Data
    public static class Amount{

        @Min(value = 100, message = "Minimum amount is 100")
        private int quantity;

        @NotBlank(message = "Currency cannot be blank")
        private String currency;
    }
}


