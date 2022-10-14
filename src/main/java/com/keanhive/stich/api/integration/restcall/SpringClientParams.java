package com.keanhive.stich.api.integration.restcall;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.MediaType;

/**
 * @author KeanHive
 * @Date 06/10/2022
 */

@Getter
@Setter
@ToString
public class SpringClientParams<T> extends SpringBaseClientParams {

    private static final long serialVersionUID = -2526295145756935318L;

    private Class<T> responseClazz;
    // Bug on PMD. Fixed in version 6.4.0 as seen from https://github.com/pmd/pmd/issues/410
    @SuppressWarnings("PMD.ImmutableField")
    private Object body;
    private MediaType contentType = MediaType.APPLICATION_JSON;

}