package com.keanhive.stich.api.integration.pojos;

import com.keanhive.stich.api.integration.enums.ProcessTypeEnum;
import com.keanhive.stich.api.integration.restcall.request.DataRequestPojo;
import com.keanhive.stich.api.integration.restcall.request.LinkPaymentRequestPojo;
import lombok.Data;

/**
 * @author KeanHive
 * @Date 06/10/2022
 */
@Data
public class UserSessionInfo {
    private String state;
    private String nonce;
    private String verifier;
    private String challenge;
    private String code;
    private String accessToken;
    private String refreshToken;
    private String scope;
    private LinkPaymentRequestPojo linkPaymentRequest;
    private DataRequestPojo dataRequest;
    private ProcessTypeEnum processTypeEnum;
}
