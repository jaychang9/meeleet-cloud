# nacos 命名空间及GROUP使用规范
nacos namespace 可以用作区分不同环境
nacos group 可以作为业务组区分

# nacos config配置创建
config/learn-common.yaml为公共配置，需要放到Nacos Config，DataId为learn-common.yaml
config/learn-xxx.yaml为各个项目的配置，同样需要放到Nacos Config,DataId为learn.xxx.yaml


# 不同环境部署时需要设置的环境变量
spring.cloud.nacos.discovery.server-addr=http://10.1.80.62:8848
spring.cloud.nacos.discovery.namespace=test
spring.cloud.nacos.discovery.group=XXX_GROUP
spring.cloud.nacos.config.namespace=test
spring.cloud.nacos.config.group=XXX_GROUP


# 若要实时更新配置值
动态刷新配置的注解@RefreshScope


# 解决网关转发到服务报 Service Unavailable错误
https://blog.csdn.net/qq_36525300/article/details/120224966
Nacos 自带 spring-cloud-starter-netflix-ribbon，而 Netflix 的 Ribbon 已进入维护阶段，从最新的 SpringCloud 2020 版本开始就需要改用 Spring cloud loadbalancer


# nacos配置yaml貌似不能用"#”来做注释会yaml格式不正确



# gateway 本例中
访问http://127.0.0.1:9999/learn-admin/echo/demo_name 网关即会将请求转发到服务ID为learn-admin的服务，本例是http://127.0.0.1:8802/echo/demo_name,转发前去掉learn-admin前缀


# 如果要使用openfeign调用那么需要引入以下依赖


        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-loadbalancer</artifactId>
        </dependency>






# token生成配置 
项目使用JWT实现access_token,关于access_token生成步骤的配置如下：

> 生成密钥库

使用JDK工具的keytool生成JKS密钥库(Java Key Store)，并将meeleet.jks放到resources目录

keytool -genkey -alias jwt -keyalg RSA -keypass 123456 -keystore meeleet.jks -storepass 123456

> 获取公钥
keytool -list -rfc --keystore meeleet.jks | openssl x509 -inform pem -pubkey


# 单元测试
## 单元测试不适用nacos config，而是使用本地配置
## 单元测试使用h2
mysql2h2-converter-tool-0.2.2.jar 可以将mysql的SQL转为H2支持的SQL


# Postman调试
请求头
Authorization  =>  Basic Y2xpZW50OjEyMzQ1Ng==
Y2xpZW50OjEyMzQ1Ng== 是 client_id:client_secret的base64编码结果


# OpenAPI3 聚合层服务增加如下OpenAPI3的配置类，以方便使用OAuth2认证后保持Token

```java
/**
 * OpenAPI 配置
 *
 * @Author jaychang
 * @Version 1.0.0
 */
@SecurityScheme(name = "securityAuth", type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
                password = @OAuthFlow(
                        tokenUrl = "${springdoc.oAuthFlow.tokenUrl}",
                        scopes = {@OAuthScope(name = "all", description = "all scope")})
        ))
@Configuration
public class OpenAPI30Configuration implements WebMvcConfigurer {

    @Bean
    public OpenAPI customizeOpenAPI() {

        OpenAPI openAPI = new OpenAPI();
        openAPI.info(new Info().title("Auth API")
                .description("Spring shop sample application")
                .version("v1.0.0")
                .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("SpringShop Wiki Documentation")
                        .url("https://springshop.wiki.github.org/docs"));



        return openAPI;
    }

}
```

```java
# 如果仅仅是JWT Token,并没有用到OAuth2可以用以下代码
        //                声明全局保持Token
        //                final String securitySchemeName = "bearerAuth";
        //                openAPI.addSecurityItem(new SecurityRequirement()
        //                        .addList(securitySchemeName))
        //                .components(new Components()
        //                        .addSecuritySchemes(securitySchemeName, new io.swagger.v3.oas.models.security.SecurityScheme()
        //                                .name(securitySchemeName)
        //                                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
        //                                .scheme("bearer")
        //                                .bearerFormat("JWT")));
```

# 网关访问api文档方式
http://localhost:9999/swagger-ui.html

# 微服务直接访问api文档
http://localhost:9999/learn-ops/swagger-ui.html
http://localhost:9999/learn-ops/swagger-ui/index.html
以上两种方式都可以


# nacos配置复制的问题
learn-auth复制到nacos config，会变成learn_auth,需要特别注意



# 认证相关问题

## 验证码登录为什么不需要提供Provider
那是因为验证码登录Authentication的类型，与用户名密码登录的Authentication的类型是一样的,都是UsernamePasswordAuthenticationToken，
org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider.supports
```
    @Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}
```
故可以复用DaoAuthenticationProvider，无需自己定义一个Provider

## 不同用户体系的认证方法 
举例：当app端、运营端、web端都需要grant_type=password的登录模式时，由于app端、web端与运营端是不同的用户体系，需要使用不同的UserDetailService

方法1： 定义不同的grant_type
       app端、web端定义grant_type = member_password
       运营端定义grant_type = operator_password
       定义多个Grant，再定义多个Provider
方法2:  grant_type都一样，grant_type = password
       参考com.meeleet.cloud.auth.security.extension.refresh.PreAuthenticatedUserDetailsService
       根据不同client_id对应不同UserDetailService,定义一个UserDetailService代理类，代理不同client_id的UserDetailService
       姑且就叫这个UserDetailService代理为MultipleUserDetailService
       
       问题是client_id哪里可以得到呢？
       看了OAuth2密码模式的认证流程，我们可以得知，需要自定义TokenGranter（在ResourceOwnerPasswordTokenGranter基础上增加client_id的信息）、因此也需要自定义AuthenticationToken(可继承UsernamePasswordAuthenticationToken，增加client_id信息)
       ,还需要自定义AuthenticationProvider(可继承DaoAuthenticationProvider，重写retrieveUser方法)
       
## 如果报以下错误
```
{
    "code": "B0001",
    "data": null,
    "msg": "Unauthorized grant type: captcha"
}
```
则相应的client_id要配置相应的authorized_grant_types，若开发时觉得不便，可以去掉缓存注解


# 自定义/oauth/token 返回格式
默认返回格式不是我们需要{"code":xxx,"data":{...},"msg":"zzz"}

## 方法一
自己写个Controller，定义一个/oauth/token方法，然后引入TokenEndpoint，方法内部直接调用它的postAccessToken方法
但是这样的话，TokenEndpoint内部定义的很多异常处理方法就失效了。无法通过自定义WebResponseExceptionTranslator来处理异常

不过也没什么关系，我们自己可以定义一个异常处理类(通过@RestControllerAdvice注解来实现)，来依样画葫芦处理各种异常

## 方法二
自定义WebResponseExceptionTranslator来处理异常


# Minio 与OkHttp 版本冲突问题
minio版本降低到8.2.2   


# nacos设置元数据
建议使用系统环境变量来设置，示例如下
spring.cloud.nacos.discovery.metadata.version=1.0
spring.cloud.nacos.discovery.metadata.weight=8