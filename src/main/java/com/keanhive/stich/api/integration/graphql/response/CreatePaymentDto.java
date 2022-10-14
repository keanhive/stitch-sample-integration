package com.keanhive.stich.api.integration.graphql.response;

import lombok.Data;
import lombok.Getter;

/**
 * @author KeanHive
 * @Date 10/10/2022
 */
@Data
public class CreatePaymentDto {

    private CreatePaymentData data;

    @Data
    public class CreatePaymentData {
        private ClientPaymentInitiationRequestCreate clientPaymentInitiationRequestCreate;

        @Data
        public class ClientPaymentInitiationRequestCreate{
            private PaymentInitiationRequest paymentInitiationRequest;

            @Data
            public class PaymentInitiationRequest{
                private String id;
                private String url;
            }
        }
    }
}
