package com.keanhive.stich.api.integration.restcall.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author KeanHive
 * @Date 18/10/2022
 */
@Data
public class RefundRequestPojo {

    @NotBlank(message = "Please provide a reasone")
    private String reason;

    @NotBlank(message = "Please provide a beneficiary reference")
    private String beneficiaryReference;

    @NotBlank(message = "Please provide a payment request ID")
    private String paymentRequestId;

    @Valid
    @NotNull(message = "Amount cannot be null")
    public Amount amount;

    @Data
    public static class Amount{

        @Min(value = 100, message = "Minimum amount is 100")
        private int quantity;

        @NotBlank(message = "Currency cannot be blank")
        private String currency;
    }
}
