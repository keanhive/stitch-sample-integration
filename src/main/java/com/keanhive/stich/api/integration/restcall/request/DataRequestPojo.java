package com.keanhive.stich.api.integration.restcall.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author KeanHive
 * @Date 18/10/2022
 */

@Data
public class DataRequestPojo {

    @NotBlank(message = "Please ensure you set account identification number")
    private String accountId;
}
