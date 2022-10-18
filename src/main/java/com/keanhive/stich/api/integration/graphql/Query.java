package com.keanhive.stich.api.integration.graphql;

import com.keanhive.stich.api.integration.restcall.request.InstantPaymentRequestPojo;
import com.keanhive.stich.api.integration.restcall.request.LinkPaymentRequestPojo;
import com.keanhive.stich.api.integration.restcall.request.RefundRequestPojo;

import java.util.HashMap;
import java.util.Map;

public class Query {

    public static String getRefundUpdatesQuery() {
        return "subscription RefundUpdates($webhookUrl: URL!, $headers: [InputHeader!])  {\n" +
                "  client(webhook: {url: $webhookUrl, headers: $headers}) {\n" +
                "    refunds {\n" +
                "      node {\n" +
                "        status {\n" +
                "          ... on RefundSubmitted {\n" +
                "            __typename\n" +
                "            date\n" +
                "          }\n" +
                "          ... on RefundCompleted {\n" +
                "            __typename\n" +
                "            date\n" +
                "            expectedSettlement\n" +
                "          }\n" +
                "          ... on RefundError {\n" +
                "            __typename\n" +
                "            date\n" +
                "            reason\n" +
                "          }\n" +
                "        }\n" +
                "        reason\n" +
                "        id\n" +
                "        created\n" +
                "        amount\n" +
                "        beneficiaryReference\n" +
                "      }\n" +
                "      eventId\n" +
                "      subscriptionId\n" +
                "      time\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    public static Map<String, Object> getRefundUpdatesVariables(String webhookUrl) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("webhookUrl", webhookUrl);
        return variables;
    }

    public static String getGetRefundStatusQuery() {
        return "query GetRefundStatus($refundId: ID!) {\n" +
                "  node(id: $refundId) {\n" +
                "    ... on Refund {\n" +
                "      id\n" +
                "      status {\n" +
                "        ... on RefundPending {\n" +
                "          __typename\n" +
                "          date\n" +
                "        }\n" +
                "        ... on RefundSubmitted {\n" +
                "          __typename\n" +
                "          date\n" +
                "        }\n" +
                "        ... on RefundCompleted {\n" +
                "          __typename\n" +
                "          date\n" +
                "          expectedSettlement\n" +
                "        }\n" +
                "        ... on RefundError {\n" +
                "          __typename\n" +
                "          date\n" +
                "          reason\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    public static Map<String, Object> getGetRefundStatusVariables(String  refundId) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("refundId", refundId);
        return variables;
    }

    public static String getCreateRefundQuery() {
        return "mutation CreateRefund(\n" +
                "    $amount: MoneyInput!,\n" +
                "    $reason: RefundReason!,\n" +
                "    $nonce: String!,\n" +
                "    $beneficiaryReference: String!,\n" +
                "    $paymentRequestId: ID!\n" +
                ") {\n" +
                "  clientRefundInitiate(input: {\n" +
                "      amount: $amount,\n" +
                "      reason: $reason,\n" +
                "      nonce: $nonce,\n" +
                "      beneficiaryReference: $beneficiaryReference,\n" +
                "      paymentRequestId: $paymentRequestId\n" +
                "    }) {\n" +
                "    refund {\n" +
                "      id\n" +
                "      paymentInitiationRequest {\n" +
                "        id\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
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
        return "query DebitOrderPaymentsByBankAccount($accountId: ID!, $first: UInt, $after: Cursor) {\n" +
                "  node(id: $accountId) {\n" +
                "    ... on BankAccount {\n" +
                "      debitOrderPayments(first: $first, after: $after) {\n" +
                "        pageInfo {\n" +
                "          hasNextPage\n" +
                "          endCursor\n" +
                "        }\n" +
                "        edges {\n" +
                "          node {\n" +
                "            id\n" +
                "            amount\n" +
                "            reference\n" +
                "            date\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }
    public static String getTransactionsByBankAccountQuery() {
        return "query TransactionsByBankAccount($accountId: ID!, $first: UInt, $after: Cursor) {\n" +
                "  node(id: $accountId) {\n" +
                "    ... on BankAccount {\n" +
                "      transactions(first: $first, after: $after) {\n" +
                "        pageInfo {\n" +
                "          hasNextPage\n" +
                "          endCursor\n" +
                "        }\n" +
                "        edges {\n" +
                "          node {\n" +
                "            id\n" +
                "            amount\n" +
                "            reference\n" +
                "            description\n" +
                "            date\n" +
                "            runningBalance\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    public static Map<String, Object> getTransactionsByBankAccountVariables(String accountId) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("accountId", accountId);
        variables.put("first", 10);
        return variables;
    }
    public static String getGetAccountBalanceQuery() {
        return "query GetAccountBalances {\n" +
                "  user {\n" +
                "    bankAccounts {\n" +
                "      currentBalance\n" +
                "      availableBalance\n" +
                "      id\n" +
                "      name\n" +
                "    }\n" +
                "  }\n" +
                "}\n";
    }
    public static String getGetAccountHoldersQuery() {
        return "query GetAccountHolders {\n" +
                "  user {\n" +
                "    bankAccounts {\n" +
                "      accountHolder {\n" +
                "        __typename\n" +
                "        ... on Individual {\n" +
                "          gender\n" +
                "          fullName\n" +
                "          email\n" +
                "          familyName\n" +
                "          givenName\n" +
                "          identifyingDocument {\n" +
                "            ... on IdentityDocument {\n" +
                "              __typename\n" +
                "              country\n" +
                "              number\n" +
                "            }\n" +
                "            ... on Passport {\n" +
                "              __typename\n" +
                "              country\n" +
                "              number\n" +
                "            }\n" +
                "          }\n" +
                "          middleName\n" +
                "          nickname\n" +
                "          homeAddress {\n" +
                "            country\n" +
                "            formatted\n" +
                "            locality\n" +
                "            postalCode\n" +
                "            region\n" +
                "            streetAddress\n" +
                "          }\n" +
                "          contact {\n" +
                "            name\n" +
                "            phoneNumber\n" +
                "          }\n" +
                "        }\n" +
                "        ... on Business {\n" +
                "          registrationNumber\n" +
                "          name\n" +
                "          accountContact {\n" +
                "            name\n" +
                "            phoneNumber\n" +
                "          }\n" +
                "          businessAddress {\n" +
                "            country\n" +
                "            formatted\n" +
                "            locality\n" +
                "            postalCode\n" +
                "            streetAddress\n" +
                "            region\n" +
                "          }\n" +
                "          email\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n";
    }
    public static String getGetAccountsQuery() {
        return "query GetAccounts {\n" +
                "  user {\n" +
                "    bankAccounts {\n" +
                "      accountNumber\n" +
                "      accountType\n" +
                "      bankId\n" +
                "      branchCode\n" +
                "      id\n" +
                "      name\n" +
                "    }\n" +
                "  }\n" +
                "}\n";
    }

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
        amount.put("currency", paymentRequest.getAmount().getCurrency());

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
