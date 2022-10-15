package com.keanhive.stich.api.integration.graphql;

import com.keanhive.stich.api.integration.restcall.request.LinkPaymentRequestPojo;

import java.util.HashMap;
import java.util.Map;

public class Query {

    public static String getLinkPayUpdatesQuery() {
        return "subscription LinkPayUpdates($webhookUrl: URL!, $headers: [InputHeader!]) {\n" +
                "  client(webhook: {url: $webhookUrl, headers: $headers}) {\n" +
                "    paymentInitiations {\n" +
                "      eventId\n" +
                "      subscriptionId\n" +
                "      time\n" +
                "      node {\n" +
                "        id\n" +
                "        externalReference\n" +
                "        date\n" +
                "        status {\n" +
                "          ... on PaymentInitiationCompleted {\n" +
                "            __typename\n" +
                "            date\n" +
                "            payer {\n" +
                "              ... on PaymentInitiationBankAccountPayer {\n" +
                "                __typename\n" +
                "                accountName\n" +
                "                accountType\n" +
                "                accountNumber\n" +
                "                bankId\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "          ... on PaymentInitiationFailed {\n" +
                "            __typename\n" +
                "            date\n" +
                "            reason\n" +
                "          }\n" +
                "          ... on PaymentInitiationCancelled {\n" +
                "            __typename\n" +
                "            date\n" +
                "            reason\n" +
                "          }\n" +
                "          ... on PaymentInitiationExpired {\n" +
                "            __typename\n" +
                "            date\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    public static Map<String, Object> getLinkPayUpdatesQueryVariables(String webhookUrl) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("webhookUrl", webhookUrl);
        return variables;
    }

    public static String getRetrievePaymentInitiationQuery() {
        return "query RetrieveAllPaymentInitiations {\n" +
                "  client {\n" +
                "    paymentInitiations {\n" +
                "      edges {\n" +
                "        node {\n" +
                "          id\n" +
                "          amount\n" +
                "          beneficiaryReference\n" +
                "          payerReference\n" +
                "          externalReference\n" +
                "          date\n" +
                "          beneficiary {\n" +
                "            ... on BankBeneficiary {\n" +
                "              __typename\n" +
                "              bankAccountNumber\n" +
                "              bankId\n" +
                "              name\n" +
                "            }\n" +
                "          }\n" +
                "          status {\n" +
                "            ... on PaymentInitiationCompleted {\n" +
                "              __typename\n" +
                "              date\n" +
                "              payer {\n" +
                "                ... on PaymentInitiationBankAccountPayer {\n" +
                "                  __typename\n" +
                "                  accountName\n" +
                "                  accountNumber\n" +
                "                  accountType\n" +
                "                  bankId\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "            ... on PaymentInitiationPending {\n" +
                "              __typename\n" +
                "              id\n" +
                "              url\n" +
                "            }\n" +
                "            ... on PaymentInitiationFailed {\n" +
                "              __typename\n" +
                "              date\n" +
                "              reason\n" +
                "            }\n" +
                "            ... on PaymentInitiationExpired {\n" +
                "              __typename\n" +
                "              date\n" +
                "            }\n" +
                "            ... on PaymentInitiationCancelled {\n" +
                "              __typename\n" +
                "              id\n" +
                "              date\n" +
                "              reason\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n";
    }

    public static String getRetrievePaymentInitiationByIdQuery() {
        return "query RetrievePaymentInitiationById($paymentInitiationId: ID!) {\n" +
                "  node(id: $paymentInitiationId) {\n" +
                "    ... on PaymentInitiation {\n" +
                "      id\n" +
                "      amount\n" +
                "      beneficiary {\n" +
                "        ... on BankBeneficiary {\n" +
                "          __typename\n" +
                "          bankAccountNumber\n" +
                "          bankId\n" +
                "          name\n" +
                "        }\n" +
                "      }\n" +
                "      beneficiaryReference\n" +
                "      date\n" +
                "      externalReference\n" +
                "      payerReference\n" +
                "      status {\n" +
                "        ... on PaymentInitiationCompleted {\n" +
                "          __typename\n" +
                "          date\n" +
                "          payer {\n" +
                "            ... on PaymentInitiationBankAccountPayer {\n" +
                "              __typename\n" +
                "              accountName\n" +
                "              accountNumber\n" +
                "              accountType\n" +
                "              bankId\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "        ... on PaymentInitiationPending {\n" +
                "          __typename\n" +
                "          id\n" +
                "          url\n" +
                "        }\n" +
                "        ... on PaymentInitiationFailed {\n" +
                "          __typename\n" +
                "          date\n" +
                "          reason\n" +
                "        }\n" +
                "        ... on PaymentInitiationExpired {\n" +
                "          __typename\n" +
                "          date\n" +
                "        }\n" +
                "        ... on PaymentInitiationCancelled {\n" +
                "          __typename\n" +
                "          id\n" +
                "          date\n" +
                "          reason\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }


    public static Map<String, Object> getRetrievePaymentInitiationByIdVariables(String paymentRequestId) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("paymentInitiationId", paymentRequestId);
        return variables;
    }

    public static String getCancelPaymentQuery() {
        return "mutation CancelPaymentInitiation($paymentRequestId: ID!, $reason: String!)\n" +
                "{\n" +
                "  clientPaymentInitiationCancel(input: {requestId: $paymentRequestId, reason: $reason}) {\n" +
                "    id\n" +
                "    __typename\n" +
                "  }\n" +
                "}";
    }

    public static Map<String, Object> getCancelPaymentQueryVariables(String paymentRequestId, String reason) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("paymentRequestId", paymentRequestId);
        variables.put("reason", reason);

        return variables;
    }
    public static String getUserInitiatePaymentQuery() {
        return "mutation UserInitiatePayment(\n" +
                "    $amount: MoneyInput!,\n" +
                "    $payerReference: String!,\n" +
                "    $externalReference: String) {  \n" +
                "  userInitiatePayment(input: {\n" +
                "      amount: $amount,\n" +
                "      payerReference: $payerReference,\n" +
                "      externalReference: $externalReference\n" +
                "    }) {\n" +
                "    paymentInitiation {\n" +
                "      amount\n" +
                "      date\n" +
                "      id\n" +
                "      status {\n" +
                "        __typename\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"
                ;
    }

    public static Map<String, Object> getUserInitiatePaymentVariables(LinkPaymentRequestPojo paymentRequest) {
        Map<String, Object> amount = new HashMap<>();
        amount.put("quantity", paymentRequest.getAmount().getQuantity());
        amount.put("currency", paymentRequest.getAmount().currency);

        Map<String, Object> variables = new HashMap<>();
        variables.put("amount", amount);
        variables.put("payerReference", paymentRequest.getPayerReference());
        variables.put("beneficiaryReference", paymentRequest.getBeneficiaryReference());
        variables.put("externalReference", paymentRequest.getExternalReference());

        return variables;
    }

    public static String getLinkedAccountAndIdentityInfoQuery() {
        return "query GetLinkedAccountAndIdentityInfo {  \n" +
                "  user {\n" +
                "    paymentAuthorization {\n" +
                "      bankAccount {\n" +
                "        id\n" +
                "        name\n" +
                "        accountNumber\n" +
                "        accountType\n" +
                "        bankId\n" +
                "        accountHolder {\n" +
                "          __typename\n" +
                "          ... on Individual {\n" +
                "            fullName\n" +
                "            identifyingDocument {\n" +
                "              ... on IdentityDocument {\n" +
                "                __typename\n" +
                "                country\n" +
                "                number\n" +
                "              }\n" +
                "              ... on Passport {\n" +
                "                __typename\n" +
                "                country\n" +
                "                number\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"
                ;
    }

    public static String createAccountLinkingRequestUpdatesQuery() {
        return "mutation CreateAccountLinkingRequest(" +
                "$name: String!," +
                "$bankId: BankBeneficiaryBankId!," +
                "$accountNumber: String!," +
                "$accountType: AccountType!," +
                "$beneficiaryType: BankBeneficiaryType!," +
                "$reference: String!," +
                "$payerEmail: EmailAddress!," +
                "$payerName: String!," +
                "$payerReference: String!," +
                "$payerPhoneNumber: String!) {" +
                "  clientPaymentAuthorizationRequestCreate(input: {" +
                "    beneficiary: {" +
                "      bankAccount: {" +
                "        name: $name, " +
                "        bankId: $bankId, " +
                "        accountNumber: $accountNumber, " +
                "        accountType: $accountType, " +
                "        beneficiaryType: $beneficiaryType, " +
                "        reference: $reference" +
                "      }" +
                "    }, payer: {        " +
                "      email: $payerEmail,       " +
                "      name: $payerName, " +
                "      reference: $payerReference," +
                "      phoneNumber: $payerPhoneNumber" +
                "  }}) {" +
                "    authorizationRequestUrl" +
                "  }" +
                "}";
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
        return  "subscription InstantPayUpdates($webhookUrl: URL!, $headers: [InputHeader!]) {\n" +
                "  client(webhook: {url: $webhookUrl, headers: $headers}) {\n" +
                "    paymentInitiationRequests {\n" +
                "      node {\n" +
                "        id\n" +
                "        externalReference\n" +
                "        state {\n" +
                "          __typename\n" +
                "          ... on PaymentInitiationRequestCompleted {\n" +
                "            date\n" +
                "          }\n" +
                "          ... on PaymentInitiationRequestCancelled {\n" +
                "            __typename\n" +
                "            date\n" +
                "            reason\n" +
                "          }\n" +
                "          ... on PaymentInitiationRequestExpired {\n" +
                "            date\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "      eventId\n" +
                "      subscriptionId\n" +
                "      time\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    public static Map<String, Object> instantPayUpdatesQueryVariables(String webhookUrl) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("webhookUrl", webhookUrl);

        return variables;
    }

    public static String createPaymentRequestQuery() {
        return  "mutation CreatePaymentRequest(\n" +
                "    $amount: MoneyInput!,\n" +
                "    $payerReference: String!,\n" +
                "    $beneficiaryReference: String!,\n" +
                "    $externalReference: String,\n" +
                "    $beneficiaryName: String!,\n" +
                "    $beneficiaryBankId: BankBeneficiaryBankId!,\n" +
                "    $beneficiaryAccountNumber: String!) {\n" +
                "  clientPaymentInitiationRequestCreate(input: {\n" +
                "      amount: $amount,\n" +
                "      payerReference: $payerReference,\n" +
                "      beneficiaryReference: $beneficiaryReference,\n" +
                "      externalReference: $externalReference,\n" +
                "      beneficiary: {\n" +
                "          bankAccount: {\n" +
                "              name: $beneficiaryName,\n" +
                "              bankId: $beneficiaryBankId,\n" +
                "              accountNumber: $beneficiaryAccountNumber\n" +
                "          }\n" +
                "      }\n" +
                "    }) {\n" +
                "    paymentInitiationRequest {\n" +
                "      id\n" +
                "      url\n" +
                "    }\n" +
                "  }\n" +
                "}";

    }


    public static Map<String, Object> getCreatePaymentVariables() {
        Map<String, Object> amount = new HashMap<>();
        amount.put("quantity", 2);
        amount.put("currency", "ZAR");

        Map<String, Object> variables = new HashMap<>();
        variables.put("amount", amount);
        variables.put("beneficiaryAccountNumber", "123456789");
        variables.put("beneficiaryBankId", "fnb");
        variables.put("beneficiaryName", "FizzBuzz Co.");
        variables.put("beneficiaryReference", "Joe-Fizz-01");
        variables.put("externalReference", "example-e32e5478-325b-4869-a53e-2021727d2afe");
        variables.put("payerReference", "KombuchaFizz");

        return variables;
    }
}
