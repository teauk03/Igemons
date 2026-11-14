package com.igemons.adapter.in.web.common;

/**
 * API 에러 응답 커맨드
 * @param status  HTTP 상태 코드
 * @param message 에러 메시지
 */
public record ErrorResponse(
    int status, 
    String message) {}
