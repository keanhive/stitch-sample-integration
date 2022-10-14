package com.keanhive.stich.api.integration.graphql.response;

import lombok.Data;

import java.util.Date;

/**
 * @author KeanHive
 * @Date 10/10/2022
 */
@Data
public class InitiatePaymentDto {
    private UserInitiatePaymentData data;

    @Data
    private class UserInitiatePaymentData {
        private UserInitiatePayment userInitiatePayment;

        @Data
        public class UserInitiatePayment{
            private PaymentInitiation paymentInitiation;

            @Data
            public class PaymentInitiation{
                private Amount amount;
                private Date date;
                private String id;
                private Status status;

                @Data
                public class Amount{
                    private String quantity;
                    private String currency;
                }

                @Data
                private class Status{
                    private String __typename;
                }
            }
        }
    }
}
