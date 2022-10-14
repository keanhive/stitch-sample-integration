package com.keanhive.stich.api.integration.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keanhive.stich.api.integration.graphql.GraphqlRequestBody;
import com.keanhive.stich.api.integration.restcall.RestServiceResponse;
import com.keanhive.stich.api.integration.restcall.SpringClientParams;
import com.keanhive.stich.api.integration.restcall.SpringGraphQlClientParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author KeanHive
 * @Date 06/10/2022
 */

@Slf4j
public class Util {

    public <T> String graphQlPost(SpringGraphQlClientParams<T> params) {
        WebClient webClient = WebClient
                .builder()
                .defaultHeaders(httpHeaders -> {
                    Map<String, String> headerParams = params.getHeaders();
                    if (headerParams != null && !headerParams.isEmpty()) {
                        for (Map.Entry<String, String> entry : headerParams.entrySet()) {
                            httpHeaders.add(entry.getKey(), entry.getValue());
                        }
                    }
                })
                .build();

        GraphqlRequestBody graphQLRequestBody = new GraphqlRequestBody();
        graphQLRequestBody.setQuery(params.getQuery());
        graphQLRequestBody.setVariables(params.getVariables());

        try {
            ClientResponse responseSpec = webClient
                                                .post()
                                                .uri(params.getTarget())
                                                .bodyValue(graphQLRequestBody)
                                                .exchange()
                                                .block();

            Map<String, Object> data =  responseSpec.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {}).block();

            try {
                return new ObjectMapper().writeValueAsString(data);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            log.error("accountLinkingDto error:  {}", e.getMessage(), e);
        }
        return null;
    }

    public <T> RestServiceResponse<T> post(SpringClientParams<T> params, RestTemplate restTemplate) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(params.getContentType());

        Map<String, String> headerParams = params.getHeaders();
        if (headerParams != null && !headerParams.isEmpty()) {
            for (Map.Entry<String, String> entry : headerParams.entrySet()) {
                headers.add(entry.getKey(), entry.getValue());
            }
        }

        HttpEntity<Object> entity = new HttpEntity<>(params.getBody(), headers);

        ResponseEntity<T> response = restTemplate.exchange(params.getTarget(), HttpMethod.POST, entity, params.getResponseClazz());

        RestServiceResponse<T> clientResponse = new RestServiceResponse<>();
        clientResponse.setBody(response.getBody());
        clientResponse.setHttpStatusCode(response.getStatusCodeValue());

        if (HttpURLConnection.HTTP_OK != response.getStatusCodeValue()) {
            log.error("response code: {}, url: {}, request: {} ", response.getStatusCodeValue(), params.getTarget(), params.getBody());
        } else {
            log.debug("response code: {}, url: {} ", response.getStatusCodeValue(), params.getTarget());
        }

        return clientResponse;
    }

    public void launchUrlInBrowser(String url) {
        Runtime rt = Runtime.getRuntime();
        try {
            log.debug("about to launch browser with url: {}", url);
            rt.exec("open " + url);
            log.debug("done launching browser");
        } catch (IOException e) {
            log.error("unable to launch browser with url: {}", url, e);
            throw new RuntimeException(e);
        }
    }

    private static String base64UrlEncode(byte[] value) {
        return Base64.getUrlEncoder()
                .encodeToString(value)
                .replaceAll("\\+", "-")
                .replaceAll("/", "_")
                .replaceAll("=", "");
    }

    private static String sha256(String value) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(value.getBytes());

        byte[] byteData = md.digest();

        return base64UrlEncode(byteData);
    }

    public Map<String, String> generateVerifierChallengePair() {
        byte[] val = getRandomBytes(32);

        String verifier = base64UrlEncode(val);
        String challenge = sha256(verifier);

        Map<String, String> codeVerifierAndChallenge = new HashMap<>();
        codeVerifierAndChallenge.put(Constants.VERIFIER, verifier);
        codeVerifierAndChallenge.put(Constants.CHALLENGE, challenge);

        return codeVerifierAndChallenge;
    }

    public String generateRandomStateOrNonce() {
        byte[] randomBytes = getRandomBytes(32);
        return base64UrlEncode(randomBytes);
    }

    public static byte[] getRandomBytes(int noOfBytes) {
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[noOfBytes];

        random.nextBytes(randomBytes);

        return randomBytes;
    }
}
