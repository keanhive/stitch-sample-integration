package com.keanhive.stich.api.integration.graphql;

import com.keanhive.stich.api.integration.restcall.request.InstantPaymentRequestPojo;
import com.keanhive.stich.api.integration.restcall.request.LinkPaymentRequestPojo;
import com.keanhive.stich.api.integration.restcall.request.RefundRequestPojo;

import java.util.HashMap;
import java.util.Map;

public class Query {


    public static String getRefundUpdatesQuery() {
        return """
                subscription RefundUpdates($webhookUrl: URL!, $headers: [InputHeader!])  {
                  client(webhook: {url: $webhookUrl, headers: $headers}) {
                    refunds {
                      node {
                        status {
                          ... on RefundSubmitted {
                            __typename
                            date
                          }
                          ... on RefundCompleted {
                            __typename
                            date
                            expectedSettlement
                          }
                          ... on RefundError {
                            __typename
                            date
                            reason
                          }
                        }
                        reason
                        id
                        created
                        amount
                        beneficiaryReference
                      }
                      eventId
                      subscriptionId
                      time
                    }
                  }
                }
                """;
    }

    public static Map<String, Object> getRefundUpdatesVariables(String webhookUrl) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("webhookUrl", webhookUrl);
        return variables;
    }

    public static String getGetRefundStatusQuery() {
        return """
                  query GetRefundStatus($refundId: ID!) {
                  node(id: $refundId) {
                    ... on Refund {
                      id
                      status {
                        ... on RefundPending {
                          __typename
                          date
                        }
                        ... on RefundSubmitted {
                          __typename
                          date
                        }
                        ... on RefundCompleted {
                          __typename
                          date
                          expectedSettlement
                        }
                        ... on RefundError {
                          __typename
                          date
                          reason
                        }
                      }
                    }
                  }
                }
                """;
    }

    public static Map<String, Object> getGetRefundStatusVariables(String refundId) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("refundId", refundId);
        return variables;
    }

    public static String getCreateRefundQuery() {
        return """
                    mutation CreateRefund(
                    $amount: MoneyInput!,
                    $reason: RefundReason!,
                    $nonce: String!,
                    $beneficiaryReference: String!,
                    $paymentRequestId: ID!
                ) {
                  clientRefundInitiate(input: {
                      amount: $amount,
                      reason: $reason,
                      nonce: $nonce,
                      beneficiaryReference: $beneficiaryReference,
                      paymentRequestId: $paymentRequestId
                    }) {
                    refund {
                      id
                      paymentInitiationRequest {
                        id
                      }
                    }
                  }
                }
                """;
    }

    public static Map<String, Object> getCreateRefundVariables(RefundRequestPojo paymentRequest, String nonce) {
        Map<String, Object> amount = new HashMap<>();
        amount.put("quantity", paymentRequest.getAmount().getQuantity());
        amount.put("currency", paymentRequest.getAmount().getCurrency());

        Map<String, Object> variables = new HashMap<>();
        variables.put("amount", amount);
        variables.put("reason", paymentRequest.getReason());
        variables.put("nonce", nonce);
        variables.put("beneficiaryReference", paymentRequest.getBeneficiaryReference());
        variables.put("paymentRequestId", paymentRequest.getPaymentRequestId());

        return variables;
    }

    public static String getDebitOrderPaymentsByBankAccountQuery() {
        return """
                  query DebitOrderPaymentsByBankAccount($accountId: ID!, $first: UInt, $after: Cursor) {
                  node(id: $accountId) {
                    ... on BankAccount {
                      debitOrderPayments(first: $first, after: $after) {
                        pageInfo {
                          hasNextPage
                          endCursor
                        }
                        edges {
                          node {
                            id
                            amount
                            reference
                            date
                          }
                        }
                      }
                    }
                  }
                }
                """;
    }

    public static String getTransactionsByBankAccountQuery() {
        return """
                  query TransactionsByBankAccount($accountId: ID!, $first: UInt, $after: Cursor) {
                  node(id: $accountId) {
                    ... on BankAccount {
                      transactions(first: $first, after: $after) {
                        pageInfo {
                          hasNextPage
                          endCursor
                        }
                        edges {
                          node {
                            id
                            amount
                            reference
                            description
                            date
                            runningBalance
                          }
                        }
                      }
                    }
                  }
                }
                """;
    }

    public static Map<String, Object> getTransactionsByBankAccountVariables(String accountId) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("accountId", accountId);
        variables.put("first", 10);
        return variables;
    }

    public static String getGetAccountBalanceQuery() {
        return """
                  query GetAccountBalances {
                  user {
                    bankAccounts {
                      currentBalance
                      availableBalance
                      id
                      name
                    }
                  }
                }
                """;
    }

    public static String getGetAccountHoldersQuery() {
        return """
                  query GetAccountHolders {
                  user {
                    bankAccounts {
                      accountHolder {
                        __typename
                        ... on Individual {
                          gender
                          fullName
                          email
                          familyName
                          givenName
                          identifyingDocument {
                            ... on IdentityDocument {
                              __typename
                              country
                              number
                            }
                            ... on Passport {
                              __typename
                              country
                              number
                            }
                          }
                          middleName
                          nickname
                          homeAddress {
                            country
                            formatted
                            locality
                            postalCode
                            region
                            streetAddress
                          }
                          contact {
                            name
                            phoneNumber
                          }
                        }
                        ... on Business {
                          registrationNumber
                          name
                          accountContact {
                            name
                            phoneNumber
                          }
                          businessAddress {
                            country
                            formatted
                            locality
                            postalCode
                            streetAddress
                            region
                          }
                          email
                        }
                      }
                    }
                  }
                }
                """;
    }

    public static String getGetAccountsQuery() {
        return """
                  query GetAccounts {
                  user {
                    bankAccounts {
                      accountNumber
                      accountType
                      bankId
                      branchCode
                      id
                      name
                    }
                  }
                }
                """;
    }

    public static String getLinkPayUpdatesQuery() {
        return """
                  subscription LinkPayUpdates($webhookUrl: URL!, $headers: [InputHeader!]) {
                  client(webhook: {url: $webhookUrl, headers: $headers}) {
                    paymentInitiations {
                      eventId
                      subscriptionId
                      time
                      node {
                        id
                        externalReference
                        date
                        status {
                          ... on PaymentInitiationCompleted {
                            __typename
                            date
                            payer {
                              ... on PaymentInitiationBankAccountPayer {
                                __typename
                                accountName
                                accountType
                                accountNumber
                                bankId
                              }
                            }
                          }
                          ... on PaymentInitiationFailed {
                            __typename
                            date
                            reason
                          }
                          ... on PaymentInitiationCancelled {
                            __typename
                            date
                            reason
                          }
                          ... on PaymentInitiationExpired {
                            __typename
                            date
                          }
                        }
                      }
                    }
                  }
                }
                """;
    }

    public static Map<String, Object> getLinkPayUpdatesQueryVariables(String webhookUrl) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("webhookUrl", webhookUrl);
        return variables;
    }

    public static String getRetrievePaymentInitiationQuery() {
        return """
                  query RetrieveAllPaymentInitiations {
                  client {
                    paymentInitiations {
                      edges {
                        node {
                          id
                          amount
                          beneficiaryReference
                          payerReference
                          externalReference
                          date
                          beneficiary {
                            ... on BankBeneficiary {
                              __typename
                              bankAccountNumber
                              bankId
                              name
                            }
                          }
                          status {
                            ... on PaymentInitiationCompleted {
                              __typename
                              date
                              payer {
                                ... on PaymentInitiationBankAccountPayer {
                                  __typename
                                  accountName
                                  accountNumber
                                  accountType
                                  bankId
                                }
                              }
                            }
                            ... on PaymentInitiationPending {
                              __typename
                              id
                              url
                            }
                            ... on PaymentInitiationFailed {
                              __typename
                              date
                              reason
                            }
                            ... on PaymentInitiationExpired {
                              __typename
                              date
                            }
                            ... on PaymentInitiationCancelled {
                              __typename
                              id
                              date
                              reason
                            }
                          }
                        }
                      }
                    }
                  }
                }
                """;
    }

    public static String getRetrievePaymentInitiationByIdQuery() {
        return """
                  query RetrievePaymentInitiationById($paymentInitiationId: ID!) {
                  node(id: $paymentInitiationId) {
                    ... on PaymentInitiation {
                      id
                      amount
                      beneficiary {
                        ... on BankBeneficiary {
                          __typename
                          bankAccountNumber
                          bankId
                          name
                        }
                      }
                      beneficiaryReference
                      date
                      externalReference
                      payerReference
                      status {
                        ... on PaymentInitiationCompleted {
                          __typename
                          date
                          payer {
                            ... on PaymentInitiationBankAccountPayer {
                              __typename
                              accountName
                              accountNumber
                              accountType
                              bankId
                            }
                          }
                        }
                        ... on PaymentInitiationPending {
                          __typename
                          id
                          url
                        }
                        ... on PaymentInitiationFailed {
                          __typename
                          date
                          reason
                        }
                        ... on PaymentInitiationExpired {
                          __typename
                          date
                        }
                        ... on PaymentInitiationCancelled {
                          __typename
                          id
                          date
                          reason
                        }
                      }
                    }
                  }
                }
                """;
    }


    public static Map<String, Object> getRetrievePaymentInitiationByIdVariables(String paymentRequestId) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("paymentInitiationId", paymentRequestId);
        return variables;
    }

    public static String getCancelPaymentQuery() {
        return """
                mutation CancelPaymentInitiation($paymentRequestId: ID!, $reason: String!)
                {
                  clientPaymentInitiationCancel(input: {requestId: $paymentRequestId, reason: $reason}) {
                    id
                    __typename
                  }
                }
                """;
    }

    public static Map<String, Object> getCancelPaymentQueryVariables(String paymentRequestId, String reason) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("paymentRequestId", paymentRequestId);
        variables.put("reason", reason);

        return variables;
    }

    public static String getUserInitiatePaymentQuery() {
        return """
                    mutation UserInitiatePayment(
                    $amount: MoneyInput!,
                    $payerReference: String!,
                    $externalReference: String) {  
                  userInitiatePayment(input: {
                      amount: $amount,
                      payerReference: $payerReference,
                      externalReference: $externalReference
                    }) {
                    paymentInitiation {
                      amount
                      date
                      id
                      status {
                        __typename
                      }
                    }
                  }
                }
                """;
    }

    public static Map<String, Object> getUserInitiatePaymentVariables(LinkPaymentRequestPojo paymentRequest) {
        Map<String, Object> amount = new HashMap<>();
        amount.put("quantity", paymentRequest.getAmount().getQuantity());
        amount.put("currency", paymentRequest.getAmount().getCurrency());

        Map<String, Object> variables = new HashMap<>();
        variables.put("amount", amount);
        variables.put("payerReference", paymentRequest.getPayerReference());
        variables.put("beneficiaryReference", paymentRequest.getBeneficiaryReference());
        variables.put("externalReference", paymentRequest.getExternalReference());

        return variables;
    }

    public static String getLinkedAccountAndIdentityInfoQuery() {
        return """
                  query GetLinkedAccountAndIdentityInfo {  
                  user {
                    paymentAuthorization {
                      bankAccount {
                        id
                        name
                        accountNumber
                        accountType
                        bankId
                        accountHolder {
                          __typename
                          ... on Individual {
                            fullName
                            identifyingDocument {
                              ... on IdentityDocument {
                                __typename
                                country
                                number
                              }
                              ... on Passport {
                                __typename
                                country
                                number
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
                """;
    }

    public static String createAccountLinkingRequestUpdatesQuery() {
        return """
                mutation CreateAccountLinkingRequest(
                $name: String!,
                $bankId: BankBeneficiaryBankId!, 
                $accountNumber: String!, 
                $accountType: AccountType!, 
                $beneficiaryType: BankBeneficiaryType!, 
                $reference: String!, 
                $payerEmail: EmailAddress!, 
                $payerName: String!, 
                $payerReference: String!, 
                $payerPhoneNumber: String!) { 
                  clientPaymentAuthorizationRequestCreate(input: { 
                    beneficiary: { 
                      bankAccount: { 
                        name: $name,  
                        bankId: $bankId,  
                        accountNumber: $accountNumber,  
                        accountType: $accountType,  
                        beneficiaryType: $beneficiaryType,  
                        reference: $reference 
                      } 
                    }, payer: {         
                      email: $payerEmail,        
                      name: $payerName,  
                      reference: $payerReference, 
                      phoneNumber: $payerPhoneNumber 
                  }}) { 
                    authorizationRequestUrl 
                  } 
                }
                """;
    }

    public static Map<String, Object> createAccountLinkingRequestUpdatesQueryVariables(LinkPaymentRequestPojo paymentRequest) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", paymentRequest.getBeneficiary().getBankAccount().getName());
        variables.put("bankId", paymentRequest.getBeneficiary().getBankAccount().getBankId());
        variables.put("accountNumber", paymentRequest.getBeneficiary().getBankAccount().getAccountNumber());
        variables.put("accountType", paymentRequest.getAccountType());
        variables.put("beneficiaryType", paymentRequest.getBeneficiaryType());
        variables.put("reference", paymentRequest.getBeneficiaryReference());
        variables.put("payerEmail", paymentRequest.getPayerEmail());
        variables.put("payerName", paymentRequest.getPayerName());
        variables.put("payerReference", paymentRequest.getPayerReference());
        variables.put("payerPhoneNumber", paymentRequest.getPayerPhoneNumber());

        return variables;
    }

    public static String instantPayUpdatesQuery() {
        return """
                  subscription InstantPayUpdates($webhookUrl: URL!, $headers: [InputHeader!]) {
                  client(webhook: {url: $webhookUrl, headers: $headers}) {
                    paymentInitiationRequests {
                      node {
                        id
                        externalReference
                        state {
                          __typename
                          ... on PaymentInitiationRequestCompleted {
                            date
                          }
                          ... on PaymentInitiationRequestCancelled {
                            __typename
                            date
                            reason
                          }
                          ... on PaymentInitiationRequestExpired {
                            date
                          }
                        }
                      }
                      eventId
                      subscriptionId
                      time
                    }
                  }
                }
                """;
    }

    public static Map<String, Object> instantPayUpdatesQueryVariables(String webhookUrl) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("webhookUrl", webhookUrl);

        return variables;
    }

    public static String createPaymentRequestQuery() {
        return """
                    mutation CreatePaymentRequest(
                    $amount: MoneyInput!,
                    $payerReference: String!,
                    $beneficiaryReference: String!,
                    $externalReference: String,
                    $beneficiaryName: String!,
                    $beneficiaryBankId: BankBeneficiaryBankId!,
                    $beneficiaryAccountNumber: String!) {
                  clientPaymentInitiationRequestCreate(input: {
                      amount: $amount,
                      payerReference: $payerReference,
                      beneficiaryReference: $beneficiaryReference,
                      externalReference: $externalReference,
                      beneficiary: {
                          bankAccount: {
                              name: $beneficiaryName,
                              bankId: $beneficiaryBankId,
                              accountNumber: $beneficiaryAccountNumber
                          }
                      }
                    }) {
                    paymentInitiationRequest {
                      id
                      url
                    }
                  }
                }
                """;

    }


    public static Map<String, Object> getCreatePaymentVariables(InstantPaymentRequestPojo paymentRequest) {
        Map<String, Object> amount = new HashMap<>();
        amount.put("quantity", paymentRequest.getAmount().getQuantity());
        amount.put("currency", paymentRequest.getAmount().getCurrency());

        Map<String, Object> variables = new HashMap<>();
        variables.put("amount", amount);
        variables.put("payerReference", paymentRequest.getPayerReference());
        variables.put("beneficiaryReference", paymentRequest.getBeneficiaryReference());
        variables.put("externalReference", paymentRequest.getExternalReference());
        variables.put("beneficiaryAccountNumber", paymentRequest.getBeneficiary().getBankAccount().getAccountNumber());
        variables.put("beneficiaryBankId", paymentRequest.getBeneficiary().getBankAccount().getBankId());
        variables.put("beneficiaryName", paymentRequest.getBeneficiary().getBankAccount().getName());

        return variables;
    }
}
