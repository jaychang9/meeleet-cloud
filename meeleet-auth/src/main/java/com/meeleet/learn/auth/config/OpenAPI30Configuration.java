package com.meeleet.cloud.auth.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 配置
 *
 * @Author jaychang
 * @Version 1.0.0
 */
@SecuritySchemes({
        @SecurityScheme(
                name = "securityAuth",
                type = SecuritySchemeType.OAUTH2,
                scheme = "bearer",
                bearerFormat = "JWT",
                in = SecuritySchemeIn.COOKIE,
                flows = @OAuthFlows(
                        password = @OAuthFlow(
                                tokenUrl = "${springdoc.oauth-flow.token-url}",
                                refreshUrl = "${springdoc.oauth-flow.refresh-token-url}",
                                scopes = {@OAuthScope(name = "all", description = "")}

                        )
                )),
        @SecurityScheme(
                name = "bearerAuth",
                type = SecuritySchemeType.HTTP,
                scheme = "bearer",
                bearerFormat = "JWT",
                in = SecuritySchemeIn.HEADER)
})
@OpenAPIDefinition(info = @Info(title = "认证API", version = "${springdoc.info.version}"), servers = {@Server(url = "${springdoc.server-url}")},
        security = {@SecurityRequirement(name = "securityAuth"),
                @SecurityRequirement(name = "bearerAuth")
        }
)
@Configuration
public class OpenAPI30Configuration {
}
