package com.keanhive.stich.api.integration.graphql;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author KeanHive
 * @Date 10/10/2022
 */


@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy .class)
@AllArgsConstructor
@NoArgsConstructor
public class GraphqlRequestBody {

    private String query;
    private Object variables;
}
