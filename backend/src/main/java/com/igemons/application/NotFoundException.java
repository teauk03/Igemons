package com.igemons.application;

/**
 * 요청한 리소스를 찾을 수 없을 때 발생하는 예외
 * HTTP 404 매핑은 adapter에서 처리
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
