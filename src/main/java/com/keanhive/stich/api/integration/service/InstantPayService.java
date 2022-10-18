package com.keanhive.stich.api.integration.service;

import com.keanhive.stich.api.integration.cache.CacheService;
import com.keanhive.stich.api.integration.graphql.GraphqlRequestBody;
import com.keanhive.stich.api.integration.graphql.Query;
import com.keanhive.stich.api.integration.graphql.response.CreatePaymentDto;
import com.keanhive.stich.api.integration.pojos.ClientTokenResponse;
import com.keanhive.stich.api.integration.restcall.request.InstantPaymentRequestPojo;
import com.keanhive.stich.api.integration.utils.AppProperties;
import com.keanhive.stich.api.integration.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @author KeanHive
 */

@Slf4j
@Service
public class InstantPayService {

    @Autowired
    AppProperties appProperties;

    @Autowired
    CacheService cacheService;

    @Autowired
    TokenGenerationService tokenGenerationService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Util util = new Util();

    public void handleWebHook(Map<String, String> qparams) {
        log.debug("handleWebHook details {}", qparams);
    }

    public void instantPayConclusion(Map<String, String> qparams, ModelAndView modelAndView) {
        log.debug("instant pay details {}", qparams);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy HH:mm:ss a");
        modelAndView.addObject("name", "Akinkunmi");
        modelAndView.addObject("date", dateTimeFormatter.format(LocalDateTime.now()));
        modelAndView.addObject("id", qparams.get("id"));
        modelAndView.addObject("status", qparams.get("status"));
        modelAndView.addObject("externalReference", qparams.get("externalReference"));
        subscribeToWebHook();
    }

    public void subscribeToWebHook() {
        ClientTokenResponse clientTokenResponse = tokenGenerationService.handleClientTokenSession();
        WebClient webClient = WebClient
                .builder()
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set("Authorization", "Bearer " + clientTokenResponse.getAccessToken());
                    httpHeaders.set("Content-Type", "application/json");
                })
                .build();
        GraphqlRequestBody graphQLRequestBody = new GraphqlRequestBody();

        final String query = Query.instantPayUpdatesQuery();
        final Map<String, Object> variables = Query.instantPayUpdatesQueryVariables(appProperties.getInstantPayWebHookUrl());

        graphQLRequestBody.setQuery(query);
        graphQLRequestBody.setVariables(variables);

        Object instantPaymentDto = webClient.post()
                .uri(appProperties.getInstantPayGraphqlUrl())
                .bodyValue(graphQLRequestBody)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        log.debug("instantPaymentDto :  {}", instantPaymentDto);
    }

    public void makePayment(InstantPaymentRequestPojo instantPayRequestPojo) {

        ClientTokenResponse clientTokenResponse = tokenGenerationService.handleClientTokenSession();
        log.debug("clientTokenResponse: {} ======= : {}", clientTokenResponse, clientTokenResponse.getAccessToken());

        WebClient webClient = WebClient
                .builder()
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set("Authorization", "Bearer " + clientTokenResponse.getAccessToken());
                    httpHeaders.set("Content-Type", "application/json");
                })
                .build();
        GraphqlRequestBody graphQLRequestBody = new GraphqlRequestBody();

        final String query = Query.createPaymentRequestQuery();
        final Map<String, Object> variables = Query.getCreatePaymentVariables(instantPayRequestPojo);

        graphQLRequestBody.setQuery(query);
        graphQLRequestBody.setVariables(variables);

        CreatePaymentDto createPaymentDto = webClient.post()
                .uri(appProperties.getInstantPayGraphqlUrl())
                .bodyValue(graphQLRequestBody)
                .retrieve()
                .bodyToMono(CreatePaymentDto.class)
                .block();


        log.debug("createPaymentDto :  {}", createPaymentDto);

        String initiateInstantPayUrl = String.format("%s?redirect_uri=%s", createPaymentDto
                                                                                        .getData()
                                                                                        .getClientPaymentInitiationRequestCreate()
                                                                                        .getPaymentInitiationRequest()
                                                                                        .getUrl()
                                                                        ,appProperties.getInstantPayRedirectUrl());

        util.launchUrlInBrowser(initiateInstantPayUrl);
    }



}
