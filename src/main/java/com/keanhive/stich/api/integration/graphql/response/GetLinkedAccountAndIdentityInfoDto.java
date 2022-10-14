package com.keanhive.stich.api.integration.graphql.response;

import lombok.Data;

/**
 * @author KeanHive
 * @Date 10/10/2022
 */
@Data
public class GetLinkedAccountAndIdentityInfoDto {

    private GetLinkedAccountAndIdentityInfoData data;

    @Data
    public class GetLinkedAccountAndIdentityInfoData {
        private User user;

        @Data
        public class User{
            private PaymentAuthorization paymentAuthorization;

            @Data
            public class PaymentAuthorization{
                private BankAccount bankAccount;

                @Data
                public class BankAccount{
                    private String id;
                    private String name;
                    private String accountNumber;
                    private String accountType;
                    private String bankId;
                    private AccountHolder accountHolder;

                    @Data
                    public class AccountHolder{
                        private String __typename;
                        private String fullName;
                        private IdentifyingDocument identifyingDocument;

                        @Data
                        public class IdentifyingDocument{
                            private String __typename;
                            private String country;
                            private String number;
                        }
                    }
                }
            }
        }
    }
}
