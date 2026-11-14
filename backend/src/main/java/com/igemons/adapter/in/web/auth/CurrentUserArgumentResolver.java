package com.igemons.adapter.in.web.auth;

import lombok.RequiredArgsConstructor;
import com.igemons.domain.users.UserId;
import com.igemons.infrastructure.jwt.JwtService;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;


/**
 * 컨트롤러 메서드 파라미터에 선언된 {@code @CurrentUser UserId}에
 * Authorization 헤더의 JWT 토큰을 검증하여 현재 로그인한 사용자 ID를 자동으로 주입하는 리졸버
 */
@Component
@RequiredArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String BEARER = "Bearer ";

    private final JwtService jwtService;

    /**
     * 리졸버 적용 대상 파라미터인지 검사
     *
     * @param parameter 컨트롤러 메서드 파라미터 메타데이터
     * @return {@code @CurrentUser} 어노테이션이 붙어 있고, 타입이 {@link UserId}이면 {@code true}
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
            && UserId.class.equals(parameter.getParameterType());
    }

    /**
     * HTTP 요청의 Authorization 헤더에서 JWT 토큰을 추출 및 검증하여 {@link UserId} 도메인 객체로 변환
     *
     * @throws ResponseStatusException 헤더가 없거나 형식이 잘못된 경우, 또는 토큰이 유효하지 않은 경우 (401 Unauthorized)
     */
    @Override
    public UserId resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String header = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        try {
            return UserId.of(jwtService.extractUserId(header.substring(BEARER.length())));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
        }
    }
}
