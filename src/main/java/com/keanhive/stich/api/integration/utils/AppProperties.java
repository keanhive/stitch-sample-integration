package com.keanhive.stich.api.integration.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties
public class AppProperties {

    @Autowired
    private Environment env;

//    redis
    private String redisMode = Constants.REDIS_MODE_STANDALONE;
    private String redisHost;
    private String redisPassword;
    private String redisUsername;
    private String redisClusterPassword;
    private String redisClusterUsername;
    private String redisClusterUrl;
    private int redisPort;

    //    clienttoken session
    private String grantType;
    private String clientId;
    private String clientTokenScope;
    private String clientSecret;
    private String audience;

//  usertoken session
    private String userTokenAuthorizationUrl;
    private String userTokenScope;
    private String responseType;
    private String codeChallengeMethod;
    private String userTokenDataRedirectUrl;

//  usertoken session3
    private String userTokenConnectUrl;
    private String userTokenGrantType;

    //urls
    private String stitchClientTokenUrl;

    //instantPay
    private String instantPayGraphqlUrl;
    private String instantPayRedirectUrl;
    private String instantPayWebHookUrl;

    //linkPay
    private String linkPayRedirectUrl;
    private String linkPayUpdatesRedirectUrl;
    private String linkPayMultifactorRedirectUrl;
    private String linkPayUpdateRedirectUrl;

    private int cacheTime = 20;

    public String getProps(String propertyName) {
        return env.getProperty(propertyName);
    }
}
