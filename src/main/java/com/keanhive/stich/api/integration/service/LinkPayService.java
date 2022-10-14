package com.keanhive.stich.api.integration.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keanhive.stich.api.integration.cache.CacheService;
import com.keanhive.stich.api.integration.graphql.GraphqlRequestBody;
import com.keanhive.stich.api.integration.graphql.Query;
import com.keanhive.stich.api.integration.graphql.response.ClientPaymentAuthorizationDto;
import com.keanhive.stich.api.integration.graphql.response.GetLinkedAccountAndIdentityInfoDto;
import com.keanhive.stich.api.integration.graphql.response.InitiatePaymentDto;
import com.keanhive.stich.api.integration.graphql.response.InitiatePaymentErrorDto;
import com.keanhive.stich.api.integration.pojos.UserSessionInfo;
import com.keanhive.stich.api.integration.pojos.ClientTokenResponse;
import com.keanhive.stich.api.integration.restcall.SpringGraphQlClientParams;
import com.keanhive.stich.api.integration.utils.AppProperties;
import com.keanhive.stich.api.integration.utils.Constants;
import com.keanhive.stich.api.integration.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Step 1
 * Retrieve client token
 * CreateAccountLinkingRequest
 * Retrieve user tokens
 *
 * Step2
 * GetLinkedAccountInfo/GetLinkedAccountAndIdentityInfo(optional)
 * UserInitiatePayment
 * redirect users if authentication required
 *
 *
 * CancelPaymentInitiation
 * Subscribing to LinkPay Webhooks(LinkPayUpdates)
 * RetrieveAllPaymentInitiations
 * RetrievePaymentInitiationById
 *
 * @author KeanHive
 */

@Slf4j
@Service
public class LinkPayService {

    @Autowired
    AppProperties appProperties;

    @Autowired
    CacheService cacheService;

    @Autowired
    TokenGenerationService tokenGenerationService;

    private final Util util = new Util();

    /**
     * displays the response gotten from when link pay requires multifactor verification
     * @param qparams
     */
    public void linkPayMultifactor(Map<String, String> qparams) {
        log.debug("linkPayMultifactor qparams {}", qparams);
    }

    /**
     *
     * Step 1
     * Retrieve client token
     * CreateAccountLinkingRequest
     * Retrieve user tokens
     *
     * @param qparams
     * qparams holds uid used to uniquely Identify a transaction made
     */
    public void handleStep1(Map<String, String> qparams) {
        String uniqueId = qparams.get("uid");

        if (StringUtils.isBlank(uniqueId)) {
            throw new IllegalArgumentException("Please ensure you set uid");
        }

        ClientTokenResponse clientTokenResponse = tokenGenerationService.handleClientTokenSession();
        log.debug("clientTokednResponse: {} ======= : {}", clientTokenResponse, clientTokenResponse.getAccessToken());
        ClientPaymentAuthorizationDto clientPaymentAuthorizationDto = initiateCreateAccountLinkingRequest(clientTokenResponse);
        tokenGenerationService.handleUserTokenStep1(clientPaymentAuthorizationDto.getData().getClientPaymentAuthorizationRequestCreate().getAuthorizationRequestUrl(), uniqueId);
    }

    /**
     * Step2
     * GetLinkedAccountInfo/GetLinkedAccountAndIdentityInfo(optional)
     * UserInitiatePayment
     * redirect users if authentication required
     * @param userSessionInfo
     * @return
     */
    public String handleStep2(UserSessionInfo userSessionInfo) {
        log.debug("handleStep2 -==========>>>>>>>>>> {}", userSessionInfo);
        handleGetLinkedAccountAndIdentityInfo(userSessionInfo);
        handleUserInitiatePayment(userSessionInfo);
        return "complete";
    }


    /**
     * trigger UserInitiatePayment api
     * @param userSessionInfo
     */
    public void handleUserInitiatePayment(UserSessionInfo userSessionInfo) {

        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.AUTHORIZATION, String.format("Bearer %s", userSessionInfo.getAccessToken()));

        SpringGraphQlClientParams<InitiatePaymentDto> params = new SpringGraphQlClientParams<>();
        params.setQuery(Query.getUserInitiatePaymentQuery());
        params.setVariables(Query.getUserInitiatePaymentVariables());
        params.setTarget(appProperties.getInstantPayGraphqlUrl());
        params.setResponseClazz(InitiatePaymentDto.class);
        params.setHeaders(headers);

        String linkedAccountAndIdentityInfoDtoResponse = util.graphQlPost(params);
        log.debug("InitiatePaymentDto response : {}", linkedAccountAndIdentityInfoDtoResponse);

        if (StringUtils.contains(linkedAccountAndIdentityInfoDtoResponse, "errors")) {
            InitiatePaymentErrorDto initiatePaymentErrorDto = null;

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                initiatePaymentErrorDto = objectMapper.readValue(linkedAccountAndIdentityInfoDtoResponse, InitiatePaymentErrorDto.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Unable to retrieve payment initiation response, please try again", e);
            }

            if (initiatePaymentErrorDto == null) {
                throw new RuntimeException("Unable to retrieve payment initiation response, please try again 02");
            }

            String failureCode = initiatePaymentErrorDto.getErrors().get(0).getExtensions().getCode();

            switch (failureCode) {
                case "USER_INTERACTION_REQUIRED" : {
                    String userInteractionUrl = initiatePaymentErrorDto.getErrors().get(0).getExtensions().getUserInteractionUrl();
                    String launchUrl = String.format("%s?redirect_uri=%s", userInteractionUrl, appProperties.getLinkPayMultifactorRedirectUrl());
                    util.launchUrlInBrowser(launchUrl);
                    break;
                }

                case "PAYMENT_FAILED":
                default:{
                    log.error("Transaction failed with reason: {}", initiatePaymentErrorDto.getErrors().get(0).getExtensions().getReason());
                    break;
                }
            }

            return;
        }

        InitiatePaymentDto initiatePaymentDto = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            initiatePaymentDto = objectMapper.readValue(linkedAccountAndIdentityInfoDtoResponse, InitiatePaymentDto.class);
            log.debug("initiatePaymentDto : {}", initiatePaymentDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to retrieve payment initiation response, please try again", e);
        }
    }

    public void handleGetLinkedAccountAndIdentityInfo(UserSessionInfo userSessionInfo) {

        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.AUTHORIZATION, String.format("Bearer %s", userSessionInfo.getAccessToken()));

        SpringGraphQlClientParams<GetLinkedAccountAndIdentityInfoDto> params = new SpringGraphQlClientParams<>();
        params.setQuery(Query.getLinkedAccountAndIdentityInfoQuery());
        params.setTarget(appProperties.getInstantPayGraphqlUrl());
        params.setResponseClazz(GetLinkedAccountAndIdentityInfoDto.class);
        params.setHeaders(headers);

        String getLinkedAccountAndIdentityInfoDtoResponse = util.graphQlPost(params);
        log.debug("GetLinkedAccountAndIdentityInfoDto response : {}", getLinkedAccountAndIdentityInfoDtoResponse);

    }

    private ClientPaymentAuthorizationDto initiateCreateAccountLinkingRequest(ClientTokenResponse clientTokenResponse) {

        WebClient webClient = WebClient
                .builder()
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set("Authorization", "Bearer " + clientTokenResponse.getAccessToken());
                    httpHeaders.set("Content-Type", "application/json");
                })
                .build();
        GraphqlRequestBody graphQLRequestBody = new GraphqlRequestBody();

        final String query = Query.createAccountLinkingRequestUpdatesQuery();
        final Map<String, Object> variables = Query.createAccountLinkingRequestUpdatesQueryVariables();

        graphQLRequestBody.setQuery(query);
        graphQLRequestBody.setVariables(variables);

        try {
            ClientPaymentAuthorizationDto accountLinkingDto = webClient.post()
                    .uri(appProperties.getInstantPayGraphqlUrl())
                    .bodyValue(graphQLRequestBody)
                    .retrieve()
                    .bodyToMono(ClientPaymentAuthorizationDto.class)
                    .block();
            log.debug("accountLinkingDto :  {}", accountLinkingDto);

            return accountLinkingDto;
        } catch (Exception e) {
            log.error("accountLinkingDto error:  {}", e.getMessage(), e);
        }

        return null;
    }

    public String cancelPayment(Map<String, String> qparams) {
        String paymentRequestId = qparams.get("paymentRequestId");

        if (StringUtils.isBlank(paymentRequestId)) {
            throw new IllegalArgumentException("Kindly supply paymentRequestId");
        }

        ClientTokenResponse clientTokenResponse = tokenGenerationService.handleClientTokenSession();

        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.AUTHORIZATION, String.format("Bearer %s", clientTokenResponse.getAccessToken()));

        SpringGraphQlClientParams<InitiatePaymentDto> params = new SpringGraphQlClientParams<>();
        params.setQuery(Query.getCancelPaymentQuery());
        params.setVariables(Query.getCancelPaymentQueryVariables(paymentRequestId, "Test reason"));
        params.setTarget(appProperties.getInstantPayGraphqlUrl());
        params.setResponseClazz(InitiatePaymentDto.class);
        params.setHeaders(headers);

        String cancelPaymentInitiationResponse = util.graphQlPost(params);

        if (StringUtils.contains(cancelPaymentInitiationResponse, "errors")) {
            InitiatePaymentErrorDto initiatePaymentErrorDto = null;

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                initiatePaymentErrorDto = objectMapper.readValue(cancelPaymentInitiationResponse, InitiatePaymentErrorDto.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Unable to retrieve payment initiation response, please try again", e);
            }

            if (initiatePaymentErrorDto == null) {
                throw new RuntimeException("Unable to retrieve payment initiation response, please try again 02");
            }

            String message = initiatePaymentErrorDto.getErrors().get(0).getMessage();
            log.error("Cancellation message: {}", message);

            return message;
        }
        return "Success";
    }

    public String getStatus(Map<String, String> qparams) {
        String paymentRequestId = qparams.get("paymentRequestId");

        if (StringUtils.isBlank(paymentRequestId)) {
            throw new IllegalArgumentException("Kindly supply paymentRequestId");
        }

        ClientTokenResponse clientTokenResponse = tokenGenerationService.handleClientTokenSession();

        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.AUTHORIZATION, String.format("Bearer %s", clientTokenResponse.getAccessToken()));

        SpringGraphQlClientParams<InitiatePaymentDto> params = new SpringGraphQlClientParams<>();
        params.setQuery(Query.getRetrievePaymentInitiationByIdQuery());
        params.setVariables(Query.getRetrievePaymentInitiationByIdVariables(paymentRequestId));
        params.setTarget(appProperties.getInstantPayGraphqlUrl());
        params.setResponseClazz(InitiatePaymentDto.class);
        params.setHeaders(headers);

        String getStatusResponse = util.graphQlPost(params);

        return getStatusResponse;
    }

    public String getAllTransactionStatus() {

        ClientTokenResponse clientTokenResponse = tokenGenerationService.handleClientTokenSession();

        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.AUTHORIZATION, String.format("Bearer %s", clientTokenResponse.getAccessToken()));

        SpringGraphQlClientParams<InitiatePaymentDto> params = new SpringGraphQlClientParams<>();
        params.setQuery(Query.getRetrievePaymentInitiationQuery());
        params.setTarget(appProperties.getInstantPayGraphqlUrl());
        params.setResponseClazz(InitiatePaymentDto.class);
        params.setHeaders(headers);

        String getStatusResponse = util.graphQlPost(params);

        return getStatusResponse;
    }

    public String handleSubscribeToLinkPayUpdates() {

        ClientTokenResponse clientTokenResponse = tokenGenerationService.handleClientTokenSession();

        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.AUTHORIZATION, String.format("Bearer %s", clientTokenResponse.getAccessToken()));

        SpringGraphQlClientParams<InitiatePaymentDto> params = new SpringGraphQlClientParams<>();
        params.setQuery(Query.getLinkPayUpdatesQuery());
        params.setVariables(Query.getLinkPayUpdatesQueryVariables(appProperties.getLinkPayUpdatesRedirectUrl()));
        params.setTarget(appProperties.getInstantPayGraphqlUrl());
        params.setResponseClazz(InitiatePaymentDto.class);
        params.setHeaders(headers);

        String getStatusResponse = util.graphQlPost(params);

        return getStatusResponse;
    }

    public void handleSubscriptionsToLinkPayUpdates(Map<String, String> qparams) {
        log.debug("handleSubscriptionsToLinkPayUpdates qparams: {}", qparams);
    }
}
