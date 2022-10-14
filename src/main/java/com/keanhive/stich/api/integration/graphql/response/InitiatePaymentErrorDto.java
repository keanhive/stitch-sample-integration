package com.keanhive.stich.api.integration.graphql.response;

import lombok.Data;

import java.util.List;

/**
 * @author KeanHive
 * @Date 10/10/2022
 */
@Data
public class InitiatePaymentErrorDto {

    private List<Error> errors;
    private InitiatePaymentError data;

    @Data
    public static class InitiatePaymentError {
        private Object userInitiatePayment;
    }

    @Data
    public static class Error{
        private String message;
        private List<Location> locations;
        private List<String> path;
        private Extensions extensions;

        @Data
        public static class Extensions{
            private String userInteractionUrl;
            private String id;
            private String code;
            private String reason;
        }

        @Data
        public static class Location{
            private int line;
            private int column;
        }
    }
}
