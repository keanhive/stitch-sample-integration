package com.keanhive.stich.api.integration.restcall;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author KeanHive
 * @Date 06/10/2022
 */

@Getter
@Setter
@ToString
public class RestServiceResponse<T> implements Serializable {

    private static final long serialVersionUID = -4028298078590845414L;

    private int httpStatusCode;
    private T body;

}
