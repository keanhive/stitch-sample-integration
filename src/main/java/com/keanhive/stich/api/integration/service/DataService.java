package com.keanhive.stich.api.integration.service;

import com.keanhive.stich.api.integration.cache.CacheService;
import com.keanhive.stich.api.integration.enums.ProcessTypeEnum;
import com.keanhive.stich.api.integration.graphql.Query;
import com.keanhive.stich.api.integration.graphql.response.InitiatePaymentDto;
import com.keanhive.stich.api.integration.pojos.UserSessionInfo;
import com.keanhive.stich.api.integration.restcall.SpringGraphQlClientParams;
import com.keanhive.stich.api.integration.restcall.request.DataRequestPojo;
import com.keanhive.stich.api.integration.utils.AppProperties;
import com.keanhive.stich.api.integration.utils.Constants;
import com.keanhive.stich.api.integration.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author KeanHive
 * @Date 17/10/2022
 */

@Slf4j
@Service
public class DataService {

    @Autowired
    AppProperties appProperties;

    @Autowired
    TokenGenerationService tokenGenerationService;

    @Autowired
    CacheService cacheService;

    private final Util util = new Util();

    public String initiateTokenSession(ProcessTypeEnum processTypeEnum, DataRequestPojo dataRequestPojo) {
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        userSessionInfo.setProcessTypeEnum(processTypeEnum);
        userSessionInfo.setDataRequest(dataRequestPojo);
        tokenGenerationService.handleUserTokenStep1(null, appProperties.getUserTokenDataRedirectUrl(), userSessionInfo);
        return "Inprogress";
    }

    public String getAccounts(UserSessionInfo userSessionInfo) {
        return handleDataCall(userSessionInfo, Query.getGetAccountsQuery(), null);
    }

    public String getAccountHolders(UserSessionInfo userSessionInfo) {
        return handleDataCall(userSessionInfo, Query.getGetAccountHoldersQuery(), null);
    }

    public String getAccountBalance(UserSessionInfo userSessionInfo) {
        return handleDataCall(userSessionInfo, Query.getGetAccountBalanceQuery(), null);
    }

    public String getTransactionsByBankAccountId(UserSessionInfo userSessionInfo) {
        return handleDataCall(userSessionInfo, Query.getTransactionsByBankAccountQuery(), Query.getTransactionsByBankAccountVariables(userSessionInfo.getDataRequest().getAccountId()));
    }

    public String getDebitOrderByBankAccountId(UserSessionInfo userSessionInfo) {
        return handleDataCall(userSessionInfo, Query.getDebitOrderPaymentsByBankAccountQuery(), Query.getTransactionsByBankAccountVariables(userSessionInfo.getDataRequest().getAccountId()));
    }

    public String handleDataCall(UserSessionInfo userSessionInfo, String query, Map<String, Object> variables) {

        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.AUTHORIZATION, String.format("Bearer %s", userSessionInfo.getAccessToken()));

        SpringGraphQlClientParams<InitiatePaymentDto> params = new SpringGraphQlClientParams<>();
        params.setQuery(query);
        params.setVariables(variables);
        params.setTarget(appProperties.getInstantPayGraphqlUrl());
        params.setHeaders(headers);

        String response = util.graphQlPost(params);

        UUID uuid = UUID.randomUUID();
        String cacheKey = uuid.toString();

        log.debug("cacheKey {}, response {}",cacheKey, response);
        cacheService.setItem(cacheKey, response, 2);

        String launchUrl = String.format("%s?cachekey=%s", appProperties.getLinkPayUpdateRedirectUrl(), cacheKey);
        util.launchUrlInBrowser(launchUrl);
        return "completed";
    }
}
