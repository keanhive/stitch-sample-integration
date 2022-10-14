package com.keanhive.stich.api.integration.restcall;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * @author KeanHive
 * @Date 06/10/2022
 */

@Getter
@Setter
public class SpringBaseClientParams implements Serializable {

    private static final long serialVersionUID = -2801033712136245789L;
    protected String target;
    protected Map<String, String> headers;
}