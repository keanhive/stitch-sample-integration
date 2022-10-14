package com.keanhive.stich.api.integration.graphql.response;

import lombok.Data;

/**
 * @author KeanHive
 * @Date 10/10/2022
 */
@Data
public class ClientPaymentAuthorizationDto {

    private ClientPaymentAuthorizationRequestCreateData data;

    @Data
    public class ClientPaymentAuthorizationRequestCreateData {
        private ClientPaymentAuthorizationRequestCreate clientPaymentAuthorizationRequestCreate;

        @Data
        public class ClientPaymentAuthorizationRequestCreate{
            private String authorizationRequestUrl;
        }
    }
}
