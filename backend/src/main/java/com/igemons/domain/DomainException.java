package com.igemons.domain;

/**
 * 도메인 예외 클래스
 * 도메인 규칙 위반 시 발생하는 예외를 발생
 * HTTP 매핑은 adapter에서 처리
 */
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
    
}
