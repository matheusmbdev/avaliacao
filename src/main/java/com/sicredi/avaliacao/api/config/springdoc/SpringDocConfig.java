package com.sicredi.avaliacao.api.config.springdoc;

import com.sicredi.avaliacao.api.exceptionhandler.Problem;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration
public class SpringDocConfig {

    private static final String BAD_REQUEST_RESPONSE = "BadRequestResponse";
    private static final String NOT_FOUND_RESPONSE = "NotFoundResponse";
    private static final String INTERNAL_SERVER_ERROR_RESPONSE = "internalServerErrorResponse";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Avaliação Sicredi API")
                        .version("v1")
                        .description("Rest API")
                        .license(new License()
                                .name("SpringDoc")
                                .url("https://springdoc.org/")
                        )
                ).tags(Collections.singletonList(
                        new Tag().name("Pautas").description("Gerencia as pautas")
                )).components(new Components()
                        .schemas(gerarSchemas())
                        .responses(gerarResponses())
                );
    }

    @Bean
    public OpenApiCustomiser openApiCustomiser() {
        return openApi -> {
            openApi.getPaths()
                    .values()
                    .forEach(pathItem -> pathItem.readOperationsMap()
                            .forEach((httpMethod, operation) -> {
                                ApiResponses responses = operation.getResponses();
                                if (httpMethod == PathItem.HttpMethod.POST) {
                                    responses.addApiResponse("400", new ApiResponse().$ref(BAD_REQUEST_RESPONSE));
                                    responses.addApiResponse("500", new ApiResponse().$ref(INTERNAL_SERVER_ERROR_RESPONSE));
                                } else {
                                    responses.addApiResponse("500", new ApiResponse().$ref(INTERNAL_SERVER_ERROR_RESPONSE));
                                }
                            })
                    );
        };
    }

    private Map<String, Schema> gerarSchemas() {
        final Map<String, Schema> schemaMap = new HashMap<>();

        Map<String, Schema> problemSchema = ModelConverters.getInstance().read(Problem.class);
        Map<String, Schema> problemObjectSchema = ModelConverters.getInstance().read(Problem.Object.class);

        schemaMap.putAll(problemSchema);
        schemaMap.putAll(problemObjectSchema);

        return schemaMap;
    }

    private Map<String, ApiResponse> gerarResponses() {
        Map<String, ApiResponse> apiResponseMap = new HashMap<>();

        Content content = new Content()
                .addMediaType(APPLICATION_JSON_VALUE,
                        new MediaType().schema(new Schema<Problem>().$ref("Problema")));

        apiResponseMap.put(BAD_REQUEST_RESPONSE, new ApiResponse()
                .description("Requisição inválida")
                .content(content));

        apiResponseMap.put(NOT_FOUND_RESPONSE, new ApiResponse()
                .description("Recurso não encontrado")
                .content(content));

        apiResponseMap.put(INTERNAL_SERVER_ERROR_RESPONSE, new ApiResponse()
                .description("Erro interno no servidor")
                .content(content));

        return apiResponseMap;
    }

}
