package com.meeleet.learn.auth.security.core.clientdetails;

import com.meeleet.learn.auth.common.enums.PasswordEncoderTypeEnum;
import com.meeleet.learn.sys.pojo.dto.ClientAuthDTO;
import com.meeleet.learn.sys.rpc.ISysOauthClientDubboService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class ClientDetailsServiceImpl implements ClientDetailsService {
    @DubboReference
    private ISysOauthClientDubboService sysOauthClientDubboService;

    @Cacheable(cacheNames = "auth", key = "'oauth-client:'+#clientId")
    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        log.info("根据client_id加载客户端信息");
        try {
            ClientAuthDTO client = sysOauthClientDubboService.findOAuth2ClientById(clientId);
            if (Objects.isNull(client)) {
                throw new NoSuchClientException("No client with requested id: " + clientId);
            }
            BaseClientDetails clientDetails = new BaseClientDetails(
                    client.getClientId(),
                    client.getResourceIds(),
                    client.getScope(),
                    client.getAuthorizedGrantTypes(),
                    client.getAuthorities(),
                    client.getWebServerRedirectUri()
            );
            clientDetails.setClientSecret(PasswordEncoderTypeEnum.NOOP.getPrefix() + client.getClientSecret());
            clientDetails.setAccessTokenValiditySeconds(client.getAccessTokenValidity());
            clientDetails.setRefreshTokenValiditySeconds(client.getRefreshTokenValidity());
            return clientDetails;
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchClientException("No client with requested id: " + clientId);
        }
    }
}
