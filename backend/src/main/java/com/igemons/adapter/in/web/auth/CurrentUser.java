package com.igemons.adapter.in.web.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 컨트롤러 파라미터(UserId)에 붙이면 Authorization: Bearer 토큰에서 로그인 사용자를 주입
 * 토큰이 없거나 유효하지 않으면 401
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {}
