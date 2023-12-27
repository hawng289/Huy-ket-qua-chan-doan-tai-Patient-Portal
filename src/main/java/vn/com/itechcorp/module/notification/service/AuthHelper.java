package vn.com.itechcorp.module.notification.service;

import feign.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import vn.com.itechcorp.base.exception.APIAuthenticationException;
import vn.com.itechcorp.module.notification.remote.LoginProxy;
import vn.com.itechcorp.module.notification.service.dto.TokenResponse;
import vn.com.itechcorp.util.JsonUtils;

import java.util.HashMap;
import java.util.Map;

@Component("authHelper")
public class AuthHelper {
    private final LoginProxy loginProxy;

    @Value("${notification.username:crd}")
    private String username;

    @Value("${notification.password:P@55w0rd}")
    private String password;

    @Value("${notification.grant_type:password}")
    private String grant_type;

    @Value("${notification.client_id:Patio}")
    private String client_id;

    public AuthHelper(LoginProxy loginProxy) {
        this.loginProxy = loginProxy;
    }

    public String getAccessToken() {
        TokenResponse token = getToken(username, password, grant_type, client_id);
        if (token == null || token.isExpired())
            token = this.updateAccessTokenCache(username, password, grant_type, client_id);
        if (token == null) throw new APIAuthenticationException("Cannot get accessionToken from Mobile APP");
        return "Bearer " + token.getAccess_token();
    }

    @Cacheable(value = "token", key = "#username", unless = "#result == null")
    public TokenResponse getToken(String username, String pass, String type, String clientID) {
        Map<String,String> params = new HashMap<>();
        if (username != null) params.putIfAbsent("username",username);
        if (pass != null) params.putIfAbsent("password",pass);
        if (type != null) params.putIfAbsent("grant_type",type);
        if (clientID != null) params.putIfAbsent("client_id",clientID);
        try (Response response = loginProxy.login(params)) {
            return JsonUtils.getInstance().convertFeignResponse(response, TokenResponse.class);
        }
    }

    @CachePut(value = "token", key = "#username")
    public TokenResponse updateAccessTokenCache(String username, String pass, String type, String clientID) {
        return getToken(username, pass, type, clientID);
    }
}
