package com.igemons.adapter.out.kakao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KakaoClient {

    private final RestClient restClient;
    private final String clientId;
    private final String clientSecret;

    public KakaoClient(
        @Value("${kakao.client-id}") String clientId,
        @Value("${kakao.client-secret:}") String clientSecret
    ) {
        this.restClient = RestClient.create();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getAccessToken(String code, String redirectUri) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        if (clientSecret != null && !clientSecret.isBlank()) {
            params.add("client_secret", clientSecret);
        }

        KakaoTokenResponse response = restClient.post()
            .uri("https://kauth.kakao.com/oauth/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(params)
            .retrieve()
            .body(KakaoTokenResponse.class);

        if (response == null || response.accessToken() == null) {
            throw new IllegalStateException("카카오 액세스 토큰 발급에 실패했습니다.");
        }
        return response.accessToken();
    }

    public KakaoUserResponse getUserInfo(String accessToken) {
        KakaoUserResponse response = restClient.get()
            .uri("https://kapi.kakao.com/v2/user/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .retrieve()
            .body(KakaoUserResponse.class);

        if (response == null) {
            throw new IllegalStateException("카카오 사용자 정보 조회에 실패했습니다.");
        }
        return response;
    }
}
