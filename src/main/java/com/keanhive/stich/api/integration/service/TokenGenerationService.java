package com.keanhive.stich.api.integration.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.keanhive.stich.api.integration.akka.SpringExtension;
import com.keanhive.stich.api.integration.akka.UserSessionActor;
import com.keanhive.stich.api.integration.cache.CacheService;
import com.keanhive.stich.api.integration.enums.ProcessTypeEnum;
import com.keanhive.stich.api.integration.pojos.ClientTokenResponse;
import com.keanhive.stich.api.integration.pojos.UserSessionInfo;
import com.keanhive.stich.api.integration.pojos.UserTokenResponse;
import com.keanhive.stich.api.integration.restcall.RestServiceResponse;
import com.keanhive.stich.api.integration.restcall.SpringClientParams;
import com.keanhive.stich.api.integration.utils.AppProperties;
import com.keanhive.stich.api.integration.utils.Constants;
import com.keanhive.stich.api.integration.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.beans.Introspector;
import java.util.Map;

/**
 * @author KeanHive
 */

@Slf4j
@Service
public class TokenGenerationService {

    @Autowired
    AppProperties appProperties;

    @Autowired
    CacheService cacheService;

    @Autowired
    private ActorSystem system;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Util util = new Util();

    /**
     * handles the generation/verification of client token
     * The client tokens are cached and only refreshed once expired as token generation
     * is a cryptographically intensive process and so can slow down queries
     * if retrieved on every request.
     * @return
     */
    public ClientTokenResponse handleClientTokenSession() {

        ClientTokenResponse clientTokenResponse = null;

        String clientTokenResponseString = cacheService.getItem(Constants.CLIENT_TOKEN);
        if (StringUtils.isNotBlank(clientTokenResponseString)) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                clientTokenResponse = objectMapper.readValue(clientTokenResponseString, ClientTokenResponse.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Unable to process client session, please try again", e);
            }

            if (clientTokenResponse != null) {
                log.debug("returning existing client token from cache");
                return clientTokenResponse;
            }
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(Constants.CLIENT_ID, appProperties.getClientId());
        body.add(Constants.CLIENT_SECRET, appProperties.getClientSecret());
        body.add(Constants.SCOPE, appProperties.getClientTokenScope());
        body.add(Constants.GRANT_TYPE, appProperties.getGrantType());
        body.add("audience", appProperties.getAudience());

        SpringClientParams<ClientTokenResponse> params = new SpringClientParams<>();
        params.setBody(body);
        params.setTarget(appProperties.getStitchClientTokenUrl());
        params.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        params.setResponseClazz(ClientTokenResponse.class);

        RestServiceResponse<ClientTokenResponse> clientTokenResponseR = null;

        try {
            clientTokenResponseR = util.post(params, restTemplate);
            log.debug("clientTokenResponse: {}", clientTokenResponse);
            clientTokenResponse = clientTokenResponseR.getBody();
            cacheClientToken(clientTokenResponse);
            return clientTokenResponse;
        } catch (Exception e) {
            log.error("Exception occured while trying to process handleTokenSession req: ", e);
        }

        return null;
    }

    /**
     * Step 1 process of generating user token
     * Prepares a url with the user details and also provides a call back url
     * to be triggered by stitch api when complete.
     *
     * @param authorizationUrl
     */
    public void handleUserTokenStep1(String authorizationUrl, String redirectUrl, UserSessionInfo userSessionInfo) {
        Map<String, String> verifierChallengePair = util.generateVerifierChallengePair();

        userSessionInfo.setChallenge(verifierChallengePair.get(Constants.CHALLENGE));
        userSessionInfo.setVerifier(verifierChallengePair.get(Constants.VERIFIER));
        userSessionInfo.setState(util.generateRandomStateOrNonce());
        userSessionInfo.setNonce(util.generateRandomStateOrNonce());

        StringBuilder generatedUrl = new StringBuilder();
        generatedUrl
                .append(StringUtils.isNotBlank(authorizationUrl) ? authorizationUrl : appProperties.getUserTokenAuthorizationUrl())
                .append("?")
                .append(Constants.CLIENT_ID).append("=").append(appProperties.getClientId()).append("&")
                .append(Constants.SCOPE).append("=").append(appProperties.getUserTokenScope()).append("&")
                .append(Constants.RESPONSE_TYPE).append("=").append(appProperties.getResponseType()).append("&")
                .append(Constants.REDIRECT_URI).append("=").append(redirectUrl).append("&")
                .append(Constants.CHALLENGE).append("=").append(userSessionInfo.getChallenge()).append("&")
                .append(Constants.CODE_CHALLENGE_METHOD).append("=").append(appProperties.getCodeChallengeMethod()).append("&")
                .append(Constants.STATE).append("=").append(userSessionInfo.getState()).append("&")
                .append(Constants.NONCE).append("=").append(userSessionInfo.getNonce())
        ;

        cacheUserSessionInfo(userSessionInfo);
        util.launchUrlInBrowser(generatedUrl.toString());
    }



    /**
     * handles the generation/verification of user token
     * @param userSessionInfo
     * @return UserTokenResponse
     */
    private UserTokenResponse handleUserTokenStep2(UserSessionInfo userSessionInfo) {

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(Constants.GRANT_TYPE, appProperties.getUserTokenGrantType());
        body.add(Constants.CLIENT_ID, appProperties.getClientId());
        body.add(Constants.CODE, userSessionInfo.getCode());
        body.add(Constants.REDIRECT_URI, getRedirectUrl(userSessionInfo));
        body.add("code_verifier", userSessionInfo.getVerifier());
        body.add(Constants.CLIENT_SECRET, appProperties.getClientSecret());

        SpringClientParams<UserTokenResponse> params = new SpringClientParams<>();
        params.setBody(body);
        params.setTarget(appProperties.getStitchClientTokenUrl());
        params.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        params.setResponseClazz(UserTokenResponse.class);

        RestServiceResponse<UserTokenResponse> userTokenResponse = null;

        try {
            userTokenResponse = util.post(params, restTemplate);
            log.debug("userTokenResponse: {}", userTokenResponse);
            return userTokenResponse.getBody();
        } catch (Exception e) {
            log.error("Exception occured while trying to process handleTokenSession req: ", e);
        }

        return null;
    }

    private String getRedirectUrl(UserSessionInfo userSessionInfo) {
        if (userSessionInfo.getProcessTypeEnum() == null || userSessionInfo.getProcessTypeEnum() == ProcessTypeEnum.LINK_PAY) {
            return appProperties.getLinkPayRedirectUrl();
        }

        //todo add switch case for other usecases at a later time
        return appProperties.getUserTokenDataRedirectUrl();
    }

    public void retrieveToken(Map<String, String> queryParams) {
        log.debug("Retrieve token {}", queryParams);
        String state = queryParams.get(Constants.STATE);
        if (StringUtils.isBlank(state)) {
            throw new RuntimeException("Unable to retrieve transaction state. Please try again");
        }

        String code = queryParams.get(Constants.CODE);
        if (StringUtils.isBlank(code)) {
            throw new RuntimeException("Unable to retrieve authorization code");
        }

        String clientInfoString = cacheService.getItem(state);
        if (StringUtils.isBlank(clientInfoString)) {
            log.debug("Unable to retrieve user session for state: {}", state);
            throw new RuntimeException("g, it seems to have expired. Please try again");
        }

        UserSessionInfo userSessionInfo;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            userSessionInfo = objectMapper.readValue(clientInfoString, UserSessionInfo.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to process client session, please try again", e);
        }

        userSessionInfo.setCode(code);

        UserTokenResponse userTokenResponse = handleUserTokenStep2(userSessionInfo);

        if (userTokenResponse == null) {
            throw new RuntimeException("Unable to establish user sesssion");
        }

        userSessionInfo.setAccessToken(userTokenResponse.getAccessToken());
        userSessionInfo.setRefreshToken(userTokenResponse.getRefreshToken());
        userSessionInfo.setScope(userTokenResponse.getScope());

        cacheUserSessionInfo(userSessionInfo);

        triggerUserTokenActor(userSessionInfo);
    }

    /**
     * triggers {@link UserSessionActor} to continue with the user token flow upon successful
     * creation/verification
     *
     * @param userSessionInfo
     */
    private void triggerUserTokenActor(UserSessionInfo userSessionInfo) {
        ActorRef userToken = system
                                .actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER
                                        .get(system)
                                        .props(Introspector
                                                .decapitalize(UserSessionActor.class.getSimpleName())), String.format("userToken_%s", userSessionInfo.getState()));
        userToken.tell(userSessionInfo, ActorRef.noSender());
    }

    private void cacheUserSessionInfo(UserSessionInfo userSessionInfo) {
        try {
            ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String jsonStr = objectWriter.writeValueAsString(userSessionInfo);
            log.debug("about to cache client info with key: {} , time {}", userSessionInfo.getState(), appProperties.getCacheTime());
            cacheService.setItem(userSessionInfo.getState(), jsonStr, appProperties.getCacheTime());
        } catch (JsonProcessingException exception) {
            log.error("error caching biodata response", exception);
        }
    }

    /**
     *
     * @param clientTokenResponse
     */
    private void cacheClientToken(ClientTokenResponse clientTokenResponse) {
        try {
            ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String jsonStr = objectWriter.writeValueAsString(clientTokenResponse);
            cacheService.setItem(Constants.CLIENT_TOKEN, jsonStr, clientTokenResponse.getExpiresIn() / 72);
        } catch (JsonProcessingException exception) {
            log.error("error caching biodata response", exception);
        }
    }
}
