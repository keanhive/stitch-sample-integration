package com.keanhive.stich.api.integration.service;

import com.keanhive.stich.api.integration.graphql.Query;
import com.keanhive.stich.api.integration.pojos.ClientTokenResponse;
import com.keanhive.stich.api.integration.restcall.SpringGraphQlClientParams;
import com.keanhive.stich.api.integration.restcall.request.RefundRequestPojo;
import com.keanhive.stich.api.integration.utils.AppProperties;
import com.keanhive.stich.api.integration.utils.Constants;
import com.keanhive.stich.api.integration.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author KeanHive
 * @Date 18/10/2022
 */


@Slf4j
@Service
public class RefundService {

    @Autowired
    AppProperties appProperties;

    @Autowired
    TokenGenerationService tokenGenerationService;

    private final Util util = new Util();

    public String createRefund(RefundRequestPojo refundRequestPojo) {
        ClientTokenResponse clientTokenResponse = tokenGenerationService.handleClientTokenSession();
        log.debug("createRefund clientTokenResponse: {}", clientTokenResponse);
        subscribeToWebHook();
        return handleRefundCall(clientTokenResponse.getAccessToken(), Query.getCreateRefundQuery(), Query.getCreateRefundVariables(refundRequestPojo
                                , util.generateRandomStateOrNonce()));
    }

    public String getRefundStatus(String refundId) {
        if(StringUtils.isBlank(refundId)) {
            throw new IllegalArgumentException("Kindly provide the refund Id");
        }

        ClientTokenResponse clientTokenResponse = tokenGenerationService.handleClientTokenSession();
        log.debug("getRefundStatus clientTokenResponse: {}", clientTokenResponse);
        return handleRefundCall(clientTokenResponse.getAccessToken(), Query.getGetRefundStatusQuery(), Query.getGetRefundStatusVariables(refundId));
    }

    public String subscribeToWebHook() {
        ClientTokenResponse clientTokenResponse = tokenGenerationService.handleClientTokenSession();
        log.debug("subscribeToWebHook clientTokenResponse: {}", clientTokenResponse);
        return handleRefundCall(clientTokenResponse.getAccessToken(), Query.getRefundUpdatesQuery(), Query.getRefundUpdatesVariables(appProperties.getWebHookRedirectUrl()));
    }

    public String handleRefundCall(String accessToken, String query, Map<String, Object> variables) {

        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.AUTHORIZATION, String.format("Bearer %s", accessToken));

        SpringGraphQlClientParams<String> params = new SpringGraphQlClientParams<>();
        params.setQuery(query);
        params.setVariables(variables);
        params.setTarget(appProperties.getInstantPayGraphqlUrl());
        params.setHeaders(headers);

        return util.graphQlPost(params);
    }
}
